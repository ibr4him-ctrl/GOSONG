package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Pengaturan extends JDialog {
   private JSlider volumeSlider;
   private JLabel valueLabel;

   public Pengaturan(JFrame owner) {
      super(owner, true);
      initComponents();
      setTitle("Pengaturan");
      pack();
      setLocationRelativeTo(owner);
   }

   private void initComponents() {
      JPanel content = new JPanel(new BorderLayout(10, 10));
      JPanel center = new JPanel(new BorderLayout(5, 5));

      JLabel label = new JLabel("Volume", SwingConstants.CENTER);
      center.add(label, BorderLayout.NORTH);

      volumeSlider = new JSlider(0, 100, 50);
      volumeSlider.setMajorTickSpacing(25);
      volumeSlider.setMinorTickSpacing(5);
      volumeSlider.setPaintTicks(true);
      center.add(volumeSlider, BorderLayout.CENTER);

      valueLabel = new JLabel("50", SwingConstants.CENTER);
      center.add(valueLabel, BorderLayout.SOUTH);

      content.add(center, BorderLayout.CENTER);
      content.setPreferredSize(new Dimension(300, 150));

      volumeSlider.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e) {
            int value = volumeSlider.getValue();
            valueLabel.setText(String.valueOf(value));
         }
      });

      setLayout(new BorderLayout());
      add(content, BorderLayout.CENTER);
   }
}
