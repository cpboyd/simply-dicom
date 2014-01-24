package org.dcm4che2.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;

/**
 * Anonymizes a DICOM object.
 * 
 * @author bwallace
 *
 */
public class Anonymizer {
    private String[] femaleFirstNames = new String[]{
            "Arwen", "Anna","Amy","Betty","Christine","Deirdre","Emma","Eowyn","Elspeth","Felicity","Gertrude","Helga","Ingrid",
            "Juliet", "Karin", "Kegan","Laura","Lisbet","Maria","Nadia","Ophelia","Patience","Questa","Ruth",
            "Shannon","Tanya","Ursula","Viora","Wilma","Xena","Yelena","Zoie","Enisa", "Annette","Gunhild",
    };
    
    private String[] maleFirstNames = new String[]{
            "Aaron","Andrew","Bill","Björn", "Callum", "Darragh", "Don", "Erik", "Frank", "Frodo", "George", "Harley", "Igor", "Jeremiah","Joe", 
            "John","Kalevi", "Larry", "Mikko", "Marc", "Mohannad", "Mohammed", "Niko", "Olin", "Peter","Per", "Paul", "Quinn", "Rafe",
            "Rob", "Sebastian", "Scott", "Sean", "Thor", "Ulf", "Vilhelm", "William", "Xavier", "Yngve", "Zerah",
            "Gunter", "Bo", 
    };
    
    private String[] lastNames = new String[]{
            "Smith", "Johnson", "Williams", "Wallace", "Wiik", "Cowan", "Hussain", "Boccanfuso", "Mohan", "Dennison", "Morley",
            "Lipton", "Dobbs", "Bernard", "Lowe", "Brown", "Ristovik","Tran","Allen", "Young", "White", "Miller", "Davis",
            "Wright", "Hill", "Underhill","Green", "Richardson", "Coleman", "Simmons", "Alexander", "Russell", "Baggins",
            "Undomiel", "Took","Zeilinger", "Yang",
    };


	/** A salt value to modify the UID's etc. */
	private long salt;

	/** Create a random anonymizer - can't reproduce hte results without knowing the random value */
	public Anonymizer() {
		this(new Random().nextLong());
	}
	
	/** Create a specified anonymizer - can provide a specific salt to allow reproducibility, or use
	 * 0 for a random
	 * 1 for a daily
	 * anonymizer.
	 * @param salt
	 */
	public Anonymizer(long salt) {
		if( salt==0 ) salt = new Random().nextLong();
		else if( salt==1 ) salt = System.nanoTime() % (24l*60*60*1000*1000*1000l);
		this.salt = salt;
	}
	
    public static String SHA1(String text) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            sha1hash = md.digest();
            return encodeHex(sha1hash);
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
    
    public static String encodeHex(byte[] data) {
    	StringBuffer ret = new StringBuffer();
    	for(int i=0; i<data.length; i++) {
    		int b = (data[i] & 0xFF);
    		if( b < 16 ) ret.append("0");
    		ret.append(Integer.toHexString(b));
    	}
    	return ret.toString();
    }
    
    public static byte[] decodeHex(String hex) {
    	byte[] ret = new byte[hex.length()/2];
    	for(int i=0; i<ret.length; i++) {
    		ret[i] = (byte) Integer.parseInt(hex.substring(2*i,2*i+2),16);
    	}
    	return ret;
    }
    /** Return the long value locations at posn in the hex decoded value */
    public static long decodeLong(String hex,int posn) {
        byte[] data = decodeHex(hex);
        long ret = 0;
        for(int i=0; i<8; i++) {
            ret = (ret << 8) | (data[(posn++) % data.length] & 0xFFl); 
        }
        ret = Math.abs(ret);
        return ret;
    }
    
    /** Update an ID field - handles Patient ID, Accession#, StudyID etc */
    public static String updateId(DicomObject ds, int tag, long deident) {
        String orig = ds.getString(tag);
        if( orig==null ) orig = Integer.toHexString(tag);
        String v = SHA1(orig+deident);
        v = v.substring(0,14).replace('+','_');
        ds.putString(tag,null, v);
        return v;
    }

    /** Update a UID type field - handles Study Instance UID, SOP UID etc */
    public static String updateUID(DicomObject ds, int tag, long deident) {
        String orig = ds.getString(tag);
        String v = SHA1(orig+deident);
        String uid = "2.25."+decodeLong(v,0)+"."+decodeLong(v,8)+"."+decodeLong(v,16);
        ds.putString(tag,null, uid);
        return v;
    }
    
	public void anonymize(DicomObject ds) {
        String newPid = updateId(ds,Tag.PatientID,salt);
        ds.putString(Tag.IssuerOfPatientID,null,"Anon");
        long lastId = decodeLong(newPid,0);
        long firstId = decodeLong(newPid,1);
        String last = lastNames[(int) (lastId % lastNames.length)];
        String sex = ds.getString(Tag.PatientSex);
        boolean isFemale = "F".equalsIgnoreCase(sex);
        // Half of the time choose a female name for "Other"
        if( "O".equalsIgnoreCase(sex) || sex==null ) {
            isFemale = (firstId & 0x100)!=0;
        }
        String first;
        if( isFemale ) {
            first = femaleFirstNames[(int) (firstId % femaleFirstNames.length)]; 
        } else {
            first = maleFirstNames[(int) (firstId % maleFirstNames.length)]; 
        }
        ds.putString(Tag.PatientName,VR.PN, last+"^"+first+"^anonymous");
        Calendar cal = Calendar.getInstance();
        int duration = 1+(int) (decodeLong(newPid,1) % 35600);
        cal.add(Calendar.DAY_OF_YEAR, -duration);
        ds.putDate(Tag.PatientBirthDate, VR.DA, cal.getTime());
        String newStudyUID = updateUID(ds,Tag.StudyInstanceUID,salt);
        cal.add(Calendar.DAY_OF_YEAR, (int) (decodeLong(newStudyUID,0) % duration));
        ds.putDate(Tag.StudyDate, VR.DA, cal.getTime());
        ds.remove(Tag.OtherPatientIDs);
        ds.remove(Tag.OtherPatientIDsSequence);
        
        updateUID(ds,Tag.SeriesInstanceUID, salt);
        updateUID(ds,Tag.SOPInstanceUID, salt);
        if( ds.contains(Tag.MediaStorageSOPInstanceUID) ) updateUID(ds,Tag.MediaStorageSOPInstanceUID,salt);
        updateId(ds,Tag.AccessionNumber,salt);
        updateId(ds,Tag.StudyID,salt);		
	}
}
