
package ueod;

import java.awt.*;
import javax.swing.*;
import java.util.List;

public class RobustnessPlotter extends JPanel
{
    private static final long serialVersionUID = 1L;

    private List<RobustnessCurvePoint> points;
    private int maxunknownNumber;

    public RobustnessPlotter(List<RobustnessCurvePoint> points, int maxunknownNumber)
    {
        this.points = points;
        this.maxunknownNumber = maxunknownNumber;
        this.setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        int w = getWidth();
        int h = getHeight();
        int left = 60, right = w - 80, top = 60, bottom = h - 60;
        int right2 = w - 30;

        int maxReal = maxunknownNumber;

        // Left Y
        g2.drawLine(left, top, left, bottom);
        g2.drawLine(left, bottom, right, bottom);

        // Right Y
        g2.drawLine(right2, top, right2, bottom);
        g2.drawLine(right, bottom, right2, bottom);

        // X Axis
        for (int i = 0; i <= maxunknownNumber; i++)
        {
            int x = left + (i * (right - left) / maxunknownNumber);
            g2.drawLine(x, bottom - 5, x, bottom + 5);
            g2.drawString(String.valueOf(i), x - 5, bottom + 20);
        }

        // Axis Y left
        for (int i = 0; i <= 10; i++)
        {
            int y = bottom - (i * (bottom - top) / 10);
            g2.drawLine(left - 5, y, left + 5, y);
            g2.drawString((i * 10) + "%", left - 30, y + 5);
        }

        // Axis Y right
        for (int i = 0; i <= maxReal; i++)
        {
            int y = bottom - (i * (bottom - top) / maxReal);
            if (i % 5 == 0 || i == maxReal)
            {
                g2.drawLine(right2 - 5, y, right2 + 5, y);
                g2.drawString(String.valueOf(i), right2 + 5, y + 5);
            }
        }

        // maxAcc(BLUE)
        int prevX = -1, prevY = -1;
        for (RobustnessCurvePoint p : points)
        {
            int x = left + (p.unknownNumber * (right - left) / maxunknownNumber);
            int y = bottom - (int)(p.maxAcc * (bottom - top));

            g2.setColor(Color.BLUE);
            g2.fillOval(x - 3, y - 3, 6, 6);
            g2.drawString(String.format("%.0f%%", p.maxAcc * 100), x + 5, y - 5);

            if (prevX != -1)
                g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

        // unknownOfCommandRatio(RED)
        prevX = -1;
        prevY = -1;
        for (RobustnessCurvePoint p : points)
        {
            int x = left + (p.unknownNumber * (right - left) / maxunknownNumber);
            int y = bottom - (int)(p.unknownOfCommandRatio * (bottom - top));

            g2.setColor(Color.RED);
            g2.fillOval(x - 3, y - 3, 6, 6);
            g2.drawString(String.format("%.0f%%", p.unknownOfCommandRatio * 100), x + 5, y - 5);

            if (prevX != -1)
                g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

        // unknownOfArgumentRatio(GREEN)
        prevX = -1;
        prevY = -1;
        for (RobustnessCurvePoint p : points)
        {
            int x = left + (p.unknownNumber * (right - left) / maxunknownNumber);
            int y = bottom - (int)(p.unknownOfArgumentRatio * (bottom - top));

            g2.setColor(Color.GREEN);
            g2.fillOval(x - 3, y - 3, 6, 6);
            g2.drawString(String.format("%.0f%%", p.unknownOfArgumentRatio * 100), x + 5, y - 5);

            if (prevX != -1)
                g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

        // avgUnkonwCount (BLACK)
        prevX = -1;
        prevY = -1;
        for (RobustnessCurvePoint p : points)
        {
            int x = left + (p.unknownNumber * (right - left) / maxunknownNumber);
            int y = (int)(bottom - (p.avgUnkonwCount * (bottom - top) / maxReal));

            g2.setColor(Color.BLACK);
            g2.fillOval(x - 3, y - 3, 6, 6);
            g2.drawString(String.valueOf(p.avgUnkonwCount), x + 5, y - 5);

            if (prevX != -1)
                g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }

        // CASE
        g2.setColor(Color.BLUE);
        g2.drawString("Max Accuracy", left + 10, top + 20);
        g2.setColor(Color.RED);
        g2.drawString("Unknown Ratio", left + 10, top + 40);
        g2.setColor(Color.GREEN);
        g2.drawString("Unknown Ratio2", left + 10, top + 60);
        g2.setColor(Color.BLACK);
        g2.drawString("Real Avg Count", right2 - 60, top + 20);
    }

    public static void showPlot(List<RobustnessCurvePoint> points, int maxunknownNumber)
    {
        JFrame frame = new JFrame("Robustness Curve");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new RobustnessPlotter(points, maxunknownNumber));
        frame.pack();
        frame.setVisible(true);
    }
}