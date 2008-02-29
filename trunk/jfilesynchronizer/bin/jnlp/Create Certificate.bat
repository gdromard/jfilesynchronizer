keytool -genkey -keystore Pingus.cert -alias Pingus.key
keytool -selfcert -alias Pingus.key -keystore Pingus.cert
keytool -list -keystore Pingus.cert
jarsigner.exe -keystore Pingus.cert -storepass KeystorePassword demo.jar  Pingus.key