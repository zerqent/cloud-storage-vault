KeyGenerator keygen = KeyGenerator.getInstance("AES");
keygen.init(128); // Size of Key
SecretKey secretKey = keygen.generateKey();

KeyPairGenerator kg = KeygPairGenerator.getInstance("RSA"):
keygen.init(1024); // Size of Key
KayPair keyPair = kg.generateKeyPair();
