# Don't obfuscate public libraries
-keep class org.dcm4che3.** {
  public protected private *;
}
-keep class com.google.** {
  public protected private *;
}
-keep class org.opencv.** {
  public protected private *;
}

# Remove Logging from releases:
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
