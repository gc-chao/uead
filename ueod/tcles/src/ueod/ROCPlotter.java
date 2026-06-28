
package ueod;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class ROCPlotter extends JFrame
{
    private static final long serialVersionUID = 1L;

    private List<ROCPoint> rocPoints;

    public ROCPlotter(List<ROCPoint> rocPoints)
    {
        this.rocPoints = rocPoints;
        initializeUI();
    }

    private void initializeUI()
    {
        setTitle("ROC Curve");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);

        ROCPanel rocPanel = new ROCPanel(rocPoints);
        add(rocPanel);
    }

    public void plot()
    {
        setVisible(true);
    }

    private static class ROCPanel extends JPanel
    {
        private static final long serialVersionUID = 1L;

        private List<ROCPoint> rocPoints;
        private static final int PADDING = 80;

        public ROCPanel(List<ROCPoint> rocPoints)
        {
            this.rocPoints = rocPoints;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawROC(g2d);
        }

        private void drawROC(Graphics2D g2d)
        {
            int width = getWidth();
            int height = getHeight();
            int plotSize = Math.min(width, height) - 2 * PADDING;

            drawAxes(g2d, width, height, plotSize);
            drawDiagonal(g2d, plotSize);
            drawCurve(g2d, plotSize);
        }

        private void drawAxes(Graphics2D g2d, int width, int height, int plotSize)
        {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));

            g2d.drawLine(PADDING, height - PADDING, PADDING + plotSize, height - PADDING);
            g2d.drawLine(PADDING, height - PADDING, PADDING, height - PADDING - plotSize);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (int i = 0; i <= 5; i++)
            {
                double value = i * 0.2;
                int pos = (int)(plotSize * value);

                int x = PADDING + pos;
                g2d.drawLine(x, height - PADDING - 5, x, height - PADDING + 5);
                g2d.drawString(String.format("%.1f", value), x - 10, height - PADDING + 20);

                int y = height - PADDING - pos;
                g2d.drawLine(PADDING - 5, y, PADDING + 5, y);
                g2d.drawString(String.format("%.1f", value), PADDING - 30, y + 5);
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("False Positive Rate (FPR)", width / 2 - 70, height - 20);

            AffineTransform originalTransform = g2d.getTransform();

            g2d.rotate(-Math.PI / 2, 20, height / 2);
            g2d.drawString("True Positive Rate (TPR)", 20, height / 2);

            g2d.setTransform(originalTransform);
        }

        private void drawDiagonal(Graphics2D g2d, int plotSize)
        {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5 }, 0));
            int height = getHeight();
            g2d.drawLine(PADDING, height - PADDING, PADDING + plotSize, height - PADDING - plotSize);
        }

        private void drawCurve(Graphics2D g2d, int plotSize)
        {
            if (rocPoints == null || rocPoints.isEmpty())
                return;

            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(3));
            int height = getHeight();

            for (int i = 1; i < rocPoints.size(); i++)
            {
                ROCPoint p1 = rocPoints.get(i - 1);
                ROCPoint p2 = rocPoints.get(i);

                int x1 = PADDING + (int)(plotSize * p1.fpr);
                int y1 = height - PADDING - (int)(plotSize * p1.tpr);
                int x2 = PADDING + (int)(plotSize * p2.fpr);
                int y2 = height - PADDING - (int)(plotSize * p2.tpr);

                g2d.drawLine(x1, y1, x2, y2);
            }

            ROCPoint bestPoint = findBestROCPoint();

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));

            double[] keyThresholds = { 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 };

            for (ROCPoint point : rocPoints)
            {
                boolean isKeyPoint = false;
                for (double threshold : keyThresholds)
                {
                    if (Math.abs(point.threshold - threshold) < 0.01)
                    {
                        isKeyPoint = true;
                        break;
                    }
                }
                boolean isBestPoint = (point == bestPoint);

                if (isKeyPoint || isBestPoint)
                {
                    int x = PADDING + (int)(plotSize * point.fpr);
                    int y = height - PADDING - (int)(plotSize * point.tpr);

                    if (isBestPoint)
                    {
                        g2d.setColor(Color.GREEN);
                        Ellipse2D.Double circle = new Ellipse2D.Double(x - 6, y - 6, 12, 12);
                        g2d.fill(circle);

                        g2d.setColor(Color.DARK_GRAY);
                        g2d.setFont(new Font("Arial", Font.BOLD, 11));
                        String info = String.format("Best: θ=%.2f\nAcc=%.1f%%",
                                point.threshold, point.accuracy * 100);
                        g2d.drawString(info, x + 8, y - 10);
                    }
                    else
                    {
                        g2d.setColor(Color.RED);
                        Ellipse2D.Double circle = new Ellipse2D.Double(x - 4, y - 4, 8, 8);
                        g2d.fill(circle);

                        g2d.setColor(Color.DARK_GRAY);
                        g2d.drawString(String.format("θ=%.1f", point.threshold), x + 5, y - 5);
                    }
                }
            }
        }

        private ROCPoint findBestROCPoint()
        {
            ROCPoint bestPoint = null;
            double minDistance = Double.MAX_VALUE;

            for (ROCPoint point : rocPoints)
            {
                double distance = Math.sqrt(point.fpr * point.fpr + (1 - point.tpr) * (1 - point.tpr));

                if (distance < minDistance)
                {
                    minDistance = distance;
                    bestPoint = point;
                }
            }

            return bestPoint;
        }
    }
}