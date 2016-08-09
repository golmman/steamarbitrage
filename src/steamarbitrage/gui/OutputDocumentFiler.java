package steamarbitrage.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;

public class OutputDocumentFiler extends DocumentFilter {
	
	private int maxLines;
	
	public OutputDocumentFiler(int maxLines) {
		this.maxLines = maxLines;
	}
	
	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset,
			String string, AttributeSet attr) throws BadLocationException {
		
		super.insertString(fb, offset, string, attr);
		
		Document document = fb.getDocument();
		Element root = document.getDefaultRootElement();
		
		if (root.getElementCount() > maxLines) {
			Element first = root.getElement(0);
			document.remove(first.getStartOffset(), first.getEndOffset());
		}
	}

}
