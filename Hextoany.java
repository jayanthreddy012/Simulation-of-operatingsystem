import java.math.BigInteger;

class Hextoany {
    static String hexToBinary(String hex) {

        String bin = "";
        String binfr = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for (int i = 0; i < hex.length(); i++) {
            iHex = Integer.parseInt("" + hex.charAt(i), 16);
            binfr = Integer.toBinaryString(iHex);

            while (binfr.length() < 4) {
                binfr = "0" + binfr;
            }
            bin += binfr;
        }
        return bin;

    }

    static String Binarytohex(String binaryStr) {
        if (binaryStr == null) {
            binaryStr = "0";
        }
        int decimal = Integer.parseInt(binaryStr, 2);
        String hexStr = Integer.toString(decimal, 16);
        return hexStr;
    }

    static String hexTodecimal(String hex) {
        return new BigInteger(hex, 16).toString(10);
    }

    static int bintodecimal(String bin) {
        int decimal;
        return decimal = Integer.parseInt(bin, 2);
    }

    static String pad(String a) {
        int len = a.length();

        StringBuffer sb = new StringBuffer(a);
        sb.reverse();
        for (int i = 0; i < 16 - len; i++) {
            sb.append('0');
        }
        return sb.reverse().toString();
    }
}
