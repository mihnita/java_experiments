package com.mihnita.test.swt_clipboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StyledTextDialog {
	static final int FONT_SIZE = 16;
	static final int MARGIN_SIZE = 16;

	static final Display display = Display.getDefault();

	// Colors. They are system, no need to dispose
//	static final Color systemForegroundColor = display.getSystemColor(SWT.COLOR_DARK_BLUE);
//	static final Color systemBackgrounColor = display.getSystemColor(SWT.COLOR_YELLOW);
	static final Color systemForegroundColor = display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
	static final Color systemBackgrounColor = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	static final Color colorRed = display.getSystemColor(SWT.COLOR_RED);

	// System font, but bigger, for better visibility (and to make sure that the size is preserved)
	static Font systemFont = display.getSystemFont();
	static { // bigger font
		FontData[] fd = systemFont.getFontData();
		fd[0].setHeight(FONT_SIZE);
		systemFont = new Font(display,fd[0]);
	}

	private final static String PREVIEW_STRING = ""
			+ "\u2022 Select some (all) text\n"
			+ "\u2022 Copy to clipboard\n"
			+ "\u2022 Paste in an application that supports styled text\n"
			+ "\n"
			+ "regular rise\n"
			+ "bold italic bold_italic\n"
			+ "background_red foreground_red\n"
			+ "underline underline_red underline_double underline_link underline_squiggle underline_error\n"
			+ "strikeout strikeout_red\n"
			+ "border_default border_red border_solid border_dash border_dot\n"
			+ "consolas_0123456789 times_new_roman arial\n"
			+ "crazy_combination\n"
			;

	public void run() {
		final Shell shell = new Shell(display);
		shell.setText("Copy styled text to Clipboard");
		createContents(shell);
		shell.setMinimumSize(1024, 768);
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}

	private void createContents(final Shell shell) {
		final int columncount = 3;
		shell.setLayout(new GridLayout(columncount, true));

		final StyledText styled = new StyledText(shell, SWT.BORDER);
		styled.setForeground(systemForegroundColor);
		styled.setBackground(systemBackgrounColor);
		styled.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE);
		styled.setFont(systemFont);
		styled.setWordWrap(true);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, columncount, 1);
		styled.setLayoutData(gridData);

		addSetContentAndStyles(styled);

		// Reuse this for the buttons
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		Button button;

		button = new Button(shell, SWT.NONE);
		button.setText("Select &All");
		button.addListener(SWT.Selection, event -> styled.selectAll());
		button.setLayoutData(gridData);

		button = new Button(shell, SWT.NONE);
		button.setText("&Copy to Clipboard");
		button.addListener(SWT.Selection, event -> styled.copy());
		button.setLayoutData(gridData);

		button = new Button(shell, SWT.NONE);
		button.setText("&Dump Clipboard");
		button.addListener(SWT.Selection, event -> dumpClipboard());
		button.setLayoutData(gridData);
	}

	private static StyleRange makeRange(Matcher m) {
		TextStyle style = new TextStyle(null, null, null);
		StyleRange range = new StyleRange(style);
		range.start = m.start();
		range.length = m.end() - m.start();
		return range;
	}

	private static void addSetContentAndStyles(StyledText styled) {
		styled.setText(PREVIEW_STRING);

		final Pattern p = Pattern.compile("\\S+");
		final Matcher m = p.matcher(PREVIEW_STRING);
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			String group = m.group();
			StyleRange range = null;
			switch (group) {
				case "bold":
					range = makeRange(m);
					range.fontStyle = SWT.BOLD;
					break;
				case "italic":
					range = makeRange(m);
					range.fontStyle = SWT.ITALIC;
					break;
				case "bold_italic":
					range = makeRange(m);
					range.fontStyle = SWT.BOLD | SWT.ITALIC;
					break;
				case "underline":
					range = makeRange(m);
					range.underline = true;
					break;
				case "underline_red":
					range = makeRange(m);
					range.underline = true;
					range.underlineColor = colorRed;
					break;
				case "underline_double":
					range = makeRange(m);
					range.underline = true;
					range.underlineStyle = SWT.UNDERLINE_DOUBLE;
					break;
				case "underline_link":
					range = makeRange(m);
					range.underline = true;
					range.underlineStyle = SWT.UNDERLINE_LINK;
					break;
				case "underline_squiggle":
					range = makeRange(m);
					range.underline = true;
					range.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
					break;
				case "underline_error":
					range = makeRange(m);
					range.underline = true;
					range.underlineStyle = SWT.UNDERLINE_ERROR;
					break;
				case "background_red":
					range = makeRange(m);
					range.background = colorRed;
					break;
				case "foreground_red":
					range = makeRange(m);
					range.foreground = colorRed;
					break;
				case "rise":
					range = makeRange(m);
					range.rise = FONT_SIZE / 2;
					break;
				case "strikeout":
					range = makeRange(m);
					range.strikeout = true;
					break;
				case "strikeout_red":
					range = makeRange(m);
					range.strikeout = true;
					range.strikeoutColor = colorRed;
					break;
				case "border_default":
					range = makeRange(m);
					range.borderStyle = SWT.BORDER;
					break;
				case "border_red":
					range = makeRange(m);
					range.borderStyle = SWT.BORDER;
					range.borderColor = colorRed;
					break;
				case "border_solid":
					range = makeRange(m);
					range.borderStyle = SWT.BORDER_SOLID;
					break;
				case "border_dash":
					range = makeRange(m);
					range.borderStyle = SWT.BORDER_DASH;
					break;
				case "border_dot":
					range = makeRange(m);
					range.borderStyle = SWT.BORDER_DOT;
					break;
				case "consolas_0123456789":
					range = makeRange(m);
					range.font = new Font(display, "Consolas", FONT_SIZE, SWT.NONE);
					break;
				case "times_new_roman":
					range = makeRange(m);
					range.font = new Font(display, "Times New Roman", FONT_SIZE, SWT.NONE);
					break;
				case "arial":
					range = makeRange(m);
					range.font = new Font(display, "Arial", FONT_SIZE, SWT.NONE);
					break;
				case "crazy_combination":
					range = makeRange(m);
					range.fontStyle = SWT.BOLD | SWT.ITALIC;
					range.underline = true;
					range.underlineColor = display.getSystemColor(SWT.COLOR_BLUE);
					range.background = display.getSystemColor(SWT.COLOR_YELLOW);
					range.foreground = display.getSystemColor(SWT.COLOR_DARK_GREEN);
					range.strikeout = true;
					range.strikeoutColor = display.getSystemColor(SWT.COLOR_CYAN);
					range.borderStyle = SWT.BORDER;
					range.borderColor = display.getSystemColor(SWT.COLOR_MAGENTA);
					break;
				default:
					if (group.startsWith("\u2022")) {
						range = makeRange(m);
						// Apply style to the complete line that starts with bullet
						int endOfLine = PREVIEW_STRING.indexOf("\n", m.start());
						if (endOfLine != -1) {
							// +1 to include the newline in the styling
							range.length = endOfLine - m.start() + 1;
							FontData[] fd = systemFont.getFontData();
							fd[0].setHeight(FONT_SIZE / 2);
							range.font = new Font(display,fd[0]);
						}
					}
			}

			if (range != null) {
				styled.setStyleRange(range);
				System.out.printf("Formatting [%d, %d] : '%s' // %s%n", start, end, group, range);
			}
		}
	}

	static void dumpClipboard() {
		Clipboard clipboard = new Clipboard(display);
		
		String[] typeNames = clipboard.getAvailableTypeNames();
		TransferData[] transferTypes = clipboard.getAvailableTypes();
		int[] types = new int[transferTypes.length];
		for (int i = 0; i < transferTypes.length; i++) {
			types[i] = transferTypes[i].type;
		}

		System.out.println("SWT Clipboard content:");
		for (String typeName : typeNames) {
			System.out.println("    typeName : " + typeName);			
		}
		for (TransferData transferType : transferTypes) {
			System.out.println("    TransferData : " + transferType.type);
		}

		ClipboardDumper.dump();
	}


	static class DumpClipboardTransfer extends ByteArrayTransfer {
		public final String[] typeNames;
		public final int[] types;

		public DumpClipboardTransfer(String[] typeNames, int[] types) {
			this.typeNames = typeNames;
			this.types = types;
		}		 

		@Override
		protected int[] getTypeIds() {
			return types;
		}

		@Override
		protected String[] getTypeNames() {
			return typeNames;
		}
		
		 protected void javaToNative(Object object, TransferData transferData) {
			 System.out.println("javaToNative : " + object + " => " + transferData);
		 }
		 protected Object nativeToJava(TransferData transferData) {
			 System.out.println("nativeToJava : " + transferData);
			 return null;
		 }
	}
}
