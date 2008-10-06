keytool -genkey -keystore dromard.cert -alias dromard.key
keytool -selfcert -alias dromard.key -keystore dromard.cert
keytool -list -keystore dromard.cert
jarsigner.exe -keystore dromard.cert -storepass KeystorePassword demo.jar  dromard.key