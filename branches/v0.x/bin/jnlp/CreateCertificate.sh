keytool -genkey -keystore Pingus.cert -alias Pingus.key -keypass KeystorePassword -storepass KeystorePassword 
keytool -selfcert -alias Pingus.key -keystore Pingus.cert -storepass KeystorePassword 
keytool -list -keystore Pingus.cert -storepass KeystorePassword
#jarsigner -keystore Pingus.cert -storepass KeystorePassword demo.jar  Pingus.key