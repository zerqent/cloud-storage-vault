FileInputStream is = new FileInputStream("/file/path");
BufferedInputStream filebuffer = new BufferedInputStream(is);

MessageDigest md = MessageDigest.getInstance("SHA-256");
DigestInputStream digestInputStream = new DigestInputStream(
        filebuffer,md);
BufferedInputStream digBuffer = new BufferedInputStream(
        digestInputStream);

Cipher cipher = Cipher.getInstance("AES/CBC/PCKS5Padding");
// cipher is also inited with a random generated key, and an 
// IV which is the digest of the key
CipherInputStream cipherInputStream = new CipherInputStream(
        digBuffer, cipher);
BufferedInputStream readBuffer = new BufferedInputStream(
        cipherInputStream);

// HttpClient uploads data by reading from readBuffer

byte[] tmpdigest = md.digest();
md.reset();
md.update(tmpdigest);

// The digest (double SHA-256) from the file just uploaded)
byte[] digest = md.digest();
