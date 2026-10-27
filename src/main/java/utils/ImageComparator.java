package utils;

import java.awt.image.BufferedImage;

public final class ImageComparator {

    private static final int DIFF_COLOR = 0xFF0000;

    private ImageComparator() {
    }

    public record Result(double diffPercent, boolean sizeMismatch, BufferedImage diffImage) {
    }

    // A pixel counts as different when any RGB channel differs by more than colorTolerance (0-255).
    // The diff image is the actual screenshot faded to grey, with differing pixels painted red.
    public static Result compare(BufferedImage expected, BufferedImage actual, int colorTolerance) {
        int width = Math.max(expected.getWidth(), actual.getWidth());
        int height = Math.max(expected.getHeight(), actual.getHeight());
        boolean sizeMismatch = expected.getWidth() != actual.getWidth()
                || expected.getHeight() != actual.getHeight();

        BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        long differing = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean inExpected = x < expected.getWidth() && y < expected.getHeight();
                boolean inActual = x < actual.getWidth() && y < actual.getHeight();
                if (!inExpected || !inActual
                        || !similar(expected.getRGB(x, y), actual.getRGB(x, y), colorTolerance)) {
                    differing++;
                    diff.setRGB(x, y, DIFF_COLOR);
                }
                else {
                    diff.setRGB(x, y, faded(actual.getRGB(x, y)));
                }
            }
        }
        double diffPercent = sizeMismatch ? 100.0 : differing * 100.0 / ((long) width * height);
        return new Result(diffPercent, sizeMismatch, diff);
    }

    private static boolean similar(int rgb1, int rgb2, int tolerance) {
        for (int shift = 0; shift <= 16; shift += 8) {
            int c1 = (rgb1 >> shift) & 0xFF;
            int c2 = (rgb2 >> shift) & 0xFF;
            if (Math.abs(c1 - c2) > tolerance) {
                return false;
            }
        }
        return true;
    }

    // Light grey version of the pixel, so red differences stand out.
    private static int faded(int rgb) {
        int grey = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
        int light = 170 + grey / 3;
        return (light << 16) | (light << 8) | light;
    }
}
