public static byte SHA256d(byte[] input) {
    Messagedigest md =
        MessageDigest.getInstance("SHA-256");
    md.update(input);
    byte[] tmp = md.digest();
    md.reset();
    md.update(tmp);
    return md.digest();
}
