package org.example;

import org.example.dtos.PGNMove;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNParser {

    public static String whitePlayer;
    public static String blackPlayer;

    public static int[] algebraicToCoords(String pos) {
        int file = pos.charAt(0) - 'a';
        int rank = 8 - Character.getNumericValue(pos.charAt(1));
        return new int[]{rank, file};
    }

    public List<String> parsePGN(String pgn) {
        List<String> games = new ArrayList<>();
        String[] gameArray = pgn.split("(?=\\[Event )");

        for (String game : gameArray) {
            String trimmed = game.trim();
            if (!trimmed.isEmpty()) {
                games.add(trimmed);
            }
        }

        whitePlayer = extractTagValue(pgn, "White");
        blackPlayer = extractTagValue(pgn, "Black");


        List<String> result = new ArrayList<>();
        System.out.println(games.size());
        for (String iPgn: games) {
            iPgn = iPgn.replaceAll("(?m)^\\[.*?\\]\\s*", "");
            iPgn = iPgn.replaceAll("\\s*(1-0|0-1|1/2-1/2)\\s*$", "");
            iPgn = iPgn.replaceAll("\\s+", " ").trim();
            iPgn = iPgn.replaceAll("\\{[^}]*\\}", "");
            iPgn = iPgn.replaceAll("\\d+\\.", "");
            iPgn = iPgn.replaceAll("\\s+", " ").trim();
            iPgn = iPgn.replaceAll("\\s*(1-0|0-1|1/2-1/2|\\*)\\s*", "").trim();
            result.add(iPgn);

        }
        return result;
    }

    public List<PGNMove> parseInList(String pgn) throws InvalidPropertiesFormatException {
        String[] tokens = pgn.split(" ");
        boolean isWhite = false;
        List<PGNMove> moves = new ArrayList<>();

        for (String token : tokens) {
            PGNMove move = new PGNMove();
            isWhite = !isWhite;
            move.isWhite = isWhite;

            if (token.equals("O-O")) {
                move.isCastleKingSide = true;
                moves.add(move);
                continue;
            } else if (token.equals("O-O-O")) {
                move.isCastleQueenSide = true;
                moves.add(move);
                continue;
            }

            Pattern movePattern = Pattern.compile("([KQRBN])?([a-h]?[1-8]?)x?([a-h][1-8])(=([QRBN]))?([+#]?)");
            Matcher matcher = movePattern.matcher(token);
            if (matcher.matches()) {
                String piece = matcher.group(1);
                String disambiguation = matcher.group(2);
                String destination = matcher.group(3);
                String promotion = matcher.group(5);

                move.piece = piece != null ? piece.charAt(0) : 'P';
                move.to = algebraicToCoords(destination);
                move.isCapture = token.contains("x");
                move.isPromotion = promotion != null;
                move.disambiguation = disambiguation;
                if (move.isPromotion) move.promoteTo = promotion.charAt(0);
                moves.add(move);
            }
            else throw new InvalidPropertiesFormatException("invalid format");

        }
        return moves;
    }

//    private static Class<?> parsePiece(char c) {
//        if(c == 'R')
//            return Rook.class;
//        else if(c == 'B')
//            return Bishop.class;
//        else if(c == 'K')
//            return King.class;
//        else if(c == 'N')
//            return Knight.class;
//        else if(c == 'Q')
//            return Queen.class;
//        else return null;
//    }

//    public static char getPieceChar(Class<?> pieceClass) {
//        if (pieceClass == Rook.class)
//            return 'R';
//        else if (pieceClass == Bishop.class)
//            return 'B';
//        else if (pieceClass == King.class)
//            return 'K';
//        else if (pieceClass == Knight.class)
//            return 'N';
//        else if (pieceClass == Queen.class)
//            return 'Q';
//        else if (pieceClass == Pawn.class)
//            return 'P';
//        else
//            return '?'; // or throw exception / return '\0'
//    }

    public static List<String> splitPGNGames(String text) {
        List<String> games = new ArrayList<>();

        // Split based on "[Event" tag
        String[] rawGames = text.split("(?=\\[Event )");

        for (String raw : rawGames) {
            String trimmed = raw.trim();
            if (!trimmed.isEmpty()) {
                games.add(trimmed);
            }
        }
        return games;
    }
    private static String extractTagValue(String pgn, String tag) {
        Pattern pattern = Pattern.compile("\\[" + tag + " \"(.*?)\"\\]");
        Matcher matcher = pattern.matcher(pgn);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Not found";
    }
}
