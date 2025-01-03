package org.zzboy.util;


import org.zzboy.search.domain.Ipv6DB;

import java.io.*;

public class IpDBUtil {
    public static byte[] toBytes(Ipv6DB ipv6DB) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(ipv6DB);
            return boas.toByteArray();
        }
    }

    public static Ipv6DB bytestoIpv6DB(InputStream is) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return (Ipv6DB)ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
