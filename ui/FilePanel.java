package net.cercis.jconc.ui;

import java.awt.Frame;
import javax.swing.JPanel;

public class FilePanel extends JPanel{

	FileDialog fdialog;

	public FilePanel(Frame frame, String []str, int length,int open, String fileName, int mode){
	
		fdialog = new FileDialog(frame, str,length, open, fileName, mode);
		fdialog.pack();
		fdialog.setLocationRelativeTo(frame);
		fdialog.setVisible(true);
	}

	public String getValidatedText(){
	
		return fdialog.getValidatedText();
	}
	public boolean getStatus(){
	
		return fdialog.flag;
	}
}
