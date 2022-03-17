package encryptions;

import com.company.EncryptionAlgorithm;

public class PlayFair implements EncryptionAlgorithm {
    static class CharPair {
        final char first;
        final char second;

        CharPair(char first, char second) {
            this.first = first;
            this.second = second;
        }
    }

    static class Pos {
        final int row;
        final int col;

        Pos(int first, int second) {
            this.row = first;
            this.col = second;
        }
    }

    private String crypt(String input, String key, boolean enc) throws Exception {
        if (!isValidKey(key)) {
            throw new Exception("Key is not valid. Make sure it don't have dups or contains 'j'");
        }
        int shiftValue = (enc) ? 1 : -1;

        key = key.toLowerCase();

        char[][] matrix = new char[5][5];

        String matrixChars = key;

        //Construct the matrixChars
        char c = 'a';
        while (matrixChars.length() < 25) {
            if (matrixChars.indexOf(c) == -1 && c != 'j') {
                matrixChars += c;
            }
            c++;
        }

        //fill in the matrix with chars
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = matrixChars.charAt(i * 5 + j);
            }
        }

        //pre processing of the palintext
        input = input.toLowerCase();
        input = input.replaceAll(" ", "");
        input = input.replaceAll("\n", "");
        input = input.replaceAll("j", "i");

        //validate the input
        if (!input.matches("\\w+")) {
            throw new Exception("You can only use latin characters.");
        }

        //cont. preprocessing

        StringBuilder plaintextBuilder = new StringBuilder(input);
        for (int i = 0; i < plaintextBuilder.length() - 1; i += 2) {
            char c1 = plaintextBuilder.charAt(i);
            char c2 = plaintextBuilder.charAt(i + 1);
            if (c1 == c2) {
                plaintextBuilder.insert(i + 1, c1 == 'x' ? 'z' : 'x');
                //reset the loop
                i = 0;
            }
        }

        //if odd append 'x' to the end
        if (plaintextBuilder.length() % 2 != 0) {
            plaintextBuilder.append('x');
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < plaintextBuilder.length(); i += 2) {
            char c1 = plaintextBuilder.charAt(i);
            char c2 = plaintextBuilder.charAt(i + 1);

            Pos pos1 = findPos(matrix, c1);
            Pos pos2 = findPos(matrix, c2);

            if (pos1.row == pos2.row) {
                result.append(matrix[pos1.row][Math.floorMod(pos1.col + shiftValue, 5)]);
                result.append(matrix[pos2.row][Math.floorMod(pos2.col + shiftValue, 5)]);
            } else if (pos1.col == pos2.col) {
                result.append(matrix[Math.floorMod(pos1.row + shiftValue, 5)][pos1.col]);
                result.append(matrix[Math.floorMod(pos2.row + shiftValue, 5)][pos2.col]);
            } else {
                result.append(matrix[pos1.row][pos2.col]);
                result.append(matrix[pos2.row][pos1.col]);
            }
        }

        return result.toString();
    }

    private Pos findPos(char[][] matrix, char c) throws Exception {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c)
                    return new Pos(i, j);
            }
        }
        throw new Exception("Not Found");
    }

    @Override
    public String encrypt(String plaintext, String key) throws Exception {
        return crypt(plaintext, key, true);
    }


    @Override
    public String decrypt(String encrypted, String key) throws Exception {
        return crypt(encrypted, key, false);
    }

    @Override
    public boolean requireKey() {
        return true;
    }

    @Override
    public boolean isValidKey(String key) {
        //Should have no dups or have j
        if (key.isEmpty())
            return true;
        if (key.contains("j"))
            return false;
        if (!key.matches("\\w+"))
            return false;
        for (int i = 0; i < key.length(); i++) {
            for (int j = i + 1; j < key.length(); j++) {
                if (key.charAt(i) == key.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String generateKey() {
        return null;
    }

    @Override
    public String name() {
        return "Playfair Cipher";
    }

    @Override
    public String description() {
        return "The Playfair cipher or Playfair square or Wheatstone-Playfair cipher is a manual symmetric encryption technique and was the first literal digram substitution cipher. The scheme was invented in 1854 by Charles Wheatstone, but bears the name of Lord Playfair for promoting its use.";
    }

}
