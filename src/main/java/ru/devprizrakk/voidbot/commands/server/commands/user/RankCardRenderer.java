package ru.devprizrakk.voidbot.commands.server.commands.user;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class RankCardRenderer {
    public static final int W = 900;
    public static final int H = 240;

    private RankCardRenderer() {
    }

    public static byte[] render(String username,
                                String avatarUrl,
                                boolean online,
                                long level,
                                long experience,
                                long requiredExperience,
                                long totalExperience,
                                long voiceSeconds) throws IOException {
        BufferedImage canvas = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();

        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        drawBackground(g);
        drawBorder(g);

        int avatarSize = 160;
        int avatarX = 48;
        int avatarY = (H - avatarSize) / 2;

        BufferedImage avatar = loadAvatar(avatarUrl, avatarSize);
        drawAvatar(g, avatar, avatarX, avatarY, avatarSize, online);

        int textX = avatarX + avatarSize + 36;

        Font nameFont = new Font("SansSerif", Font.BOLD, 32);
        Font infoFont = new Font("SansSerif", Font.PLAIN, 20);

        g.setColor(Color.WHITE);
        g.setFont(nameFont);
        drawTruncated(g, username, textX, avatarY + 36, W - textX - 80);

        String sep = "   ◦   ";
        String line = "Ур. " + level
                + sep + "Опыт: " + experience + "/" + requiredExperience
                + sep + "Войс: " + formatTime(voiceSeconds)
                + sep + "Всего: " + totalExperience;

        g.setFont(infoFont);
        FontMetrics fm = g.getFontMetrics();

        String[] parts = line.split(java.util.regex.Pattern.quote(sep), -1);
        int partW = fm.stringWidth(parts[0]);
        int sepW = fm.stringWidth(sep);
        int drawY = avatarY + 76;
        int curX = textX;

        String[][] colored = {
                {parts[0], "#DCDCDC"},
                {parts[1], "#C8C8C8"},
                {parts[2], "#B4DCB4"},
                {parts[3], "#A0A0A0"}
        };

        for (int i = 0; i < colored.length; i++) {
            g.setColor(parseHex(colored[i][1]));
            g.drawString(colored[i][0], curX, drawY);
            curX += fm.stringWidth(colored[i][0]);
            if (i < colored.length - 1) {
                g.setColor(new Color(120, 125, 135));
                g.drawString(sep, curX, drawY);
                curX += sepW;
            }
            if (i == 0) partW = 0;
        }

        progressBar(g, textX, avatarY + 104, W - textX - 48, 18, experience, requiredExperience);

        drawLeaves(g);

        g.dispose();
        return toPng(canvas);
    }

    private static void drawBackground(Graphics2D g) {
        RoundRectangle2D card = new RoundRectangle2D.Float(0, 0, W, H, 28, 28);
        g.setColor(new Color(35, 38, 45, 245));
        g.fill(card);

        Ellipse2D glow = new Ellipse2D.Float(-100, -60, 320, 320);
        g.setPaint(new RadialGradientPaint(
                new Point2D.Float(60, 60),
                220,
                new float[]{0f, 1f},
                new Color[]{new Color(80, 120, 90, 90), new Color(0, 0, 0, 0)}));
        g.fill(glow);
        g.setPaint(null);
    }

    private static void drawBorder(Graphics2D g) {
        g.setColor(new Color(64, 70, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(1, 1, W - 3, H - 3, 28, 28);
        g.setStroke(new BasicStroke(1));
    }

    private static BufferedImage loadAvatar(String url, int size) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            Image src = ImageIO.read(is);
            if (src == null) {
                return fallbackAvatar(size);
            }
            BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, size, size, null);
            g.dispose();
            return out;
        } catch (Exception e) {
            return fallbackAvatar(size);
        }
    }

    private static BufferedImage fallbackAvatar(int size) {
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setColor(new Color(60, 70, 80));
        g.fillOval(0, 0, size, size);
        g.dispose();
        return out;
    }

    private static void drawAvatar(Graphics2D g, BufferedImage avatar, int x, int y, int size, boolean online) {
        BufferedImage rounded = makeRounded(avatar, size);

        g.setColor(new Color(20, 22, 26));
        g.fillOval(x - 4, y - 4, size + 8, size + 8);

        Area clip = new Area(new Ellipse2D.Float(x, y, size, size));
        g.setClip(clip);
        g.drawImage(rounded, x, y, size, size, null);
        g.setClip(null);

        g.setColor(new Color(120, 130, 140));
        g.setStroke(new BasicStroke(2));
        g.drawOval(x, y, size, size);

        int badge = 26;
        int bx = x + size - badge + 4;
        int by = y + size - badge + 4;
        g.setPaint(online ? new Color(60, 200, 90) : new Color(180, 40, 40));
        g.fillOval(bx, by, badge, badge);
        g.setColor(new Color(20, 22, 26));
        g.setStroke(new BasicStroke(3));
        g.drawOval(bx, by, badge, badge);
        g.setStroke(new BasicStroke(1));
    }

    private static BufferedImage makeRounded(BufferedImage src, int size) {
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(new Ellipse2D.Float(0, 0, size, size));
        g.drawImage(src, 0, 0, size, size, null);
        g.dispose();
        return out;
    }

    private static void drawTruncated(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String ellipsis = "...";
        if (fm.stringWidth(text) <= maxWidth) {
            g.drawString(text, x, y);
            return;
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() > 0 && fm.stringWidth(sb.toString() + ellipsis) > maxWidth) {
            sb.deleteCharAt(sb.length() - 1);
        }
        g.drawString(sb + ellipsis, x, y);
    }

    private static Color parseHex(String hex) {
        return Color.decode(hex);
    }

    private static void progressBar(Graphics2D g, int x, int y, int width, int height,
                                    long value, long required) {
        double ratio = required > 0 ? Math.min(1.0, Math.max(0.0, (double) value / (double) required)) : 0;

        RoundRectangle2D bg = new RoundRectangle2D.Float(x, y, width, height, height, height);
        g.setColor(new Color(28, 30, 34));
        g.fill(bg);

        int filled = (int) Math.round((width - 4) * ratio);
        if (filled > 0) {
            RoundRectangle2D fg = new RoundRectangle2D.Float(x + 2, y + 2,
                    Math.max(height, filled), height - 4, (height - 4) / 2f, (height - 4) / 2f);
            g.setColor(new Color(110, 220, 90));
            g.fill(fg);
        }

        g.setColor(new Color(80, 86, 96));
        g.setStroke(new BasicStroke(1));
        g.draw(bg);
        g.setStroke(new BasicStroke(1));
    }

    private static void drawLeaves(Graphics2D g) {
        g = (Graphics2D) g.create();
        int cx = W - 90;
        int cy = 50;
        g.translate(cx, cy);
        g.rotate(Math.toRadians(-20));
        g.setColor(new Color(110, 180, 90, 200));

        for (int i = 0; i < 4; i++) {
            AffineTransform t = g.getTransform();
            g.translate(i * 18, i * 26);
            g.rotate(Math.toRadians(28 * i));
            Ellipse2D leaf = new Ellipse2D.Double(0, 0, 42, 18);
            g.fill(leaf);
            g.setColor(new Color(90, 150, 70, 200));
            g.draw(leaf);
            g.setColor(new Color(110, 180, 90, 200));
            if (i > 0) g.setTransform(t);
        }
        g.dispose();
    }

    private static byte[] toPng(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }

    public static String formatTime(long seconds) {
        if (seconds <= 0) return "0м";
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long mins = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("д ");
        if (hours > 0) sb.append(hours).append("ч ");
        if (mins > 0) sb.append(mins).append("м ");
        if (sb.length() == 0) {
            sb.append(secs).append("с");
        }
        return sb.toString().trim();
    }

    // Подавляем возможные предупреждения о шрифтах без discourage unused
    @SuppressWarnings("unused")
    private static boolean fontsReady() {
        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        return true;
    }
}