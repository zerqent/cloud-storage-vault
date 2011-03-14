// Signing
Signature sign = Signature.getInstance("RSA");
sign.init(privateKey);
sign.update(data);
byte[] signature = sign.sign();

// Verifying
Signature verify = Signature.getInstance("RSA");
sign.initVerify(publicKey);
sign.update(data);
boolean valid = sign.verify(signature);
