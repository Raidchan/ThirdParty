package net.raiid.thirdparty.client;

public final class Json {

    public static String getString(String json, String key) {
        int i = json.indexOf("\""+key+"\"");
        if (i < 0) return null;
        int c = json.indexOf(':', i);
        if (c < 0) return null;
        int q1 = json.indexOf('\"', c + 1);
        if (q1 < 0) return null;
        int q2 = json.indexOf('\"', q1 + 1);
        if (q2 < 0) return null;
        return json.substring(q1 + 1, q2);
    }

    public static String obj(String... kv) {
        if (kv.length % 2 != 0)
        	throw new IllegalArgumentException("kv length must be even");
        StringBuilder sb = new StringBuilder().append('{');
        for (int i = 0; i < kv.length; i += 2) {
            if (i > 0)
            	sb.append(',');
            sb.append('\"').append(kv[i]).append('\"').append(':');
            String v = kv[i + 1];
            if (v != null && v.startsWith("#"))
            	sb.append(v.substring(1));
            else
            	sb.append('\"').append(escape(v)).append('\"');
        }
        return sb.append('}').toString();
    }

    public static String escape(String s) {
        return s == null? "" : s.replace("\\","\\\\").replace("\"","\\\"");
    }

}
