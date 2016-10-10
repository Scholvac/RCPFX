package de.sos.rcp.ui.editors.text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Scanner;
import java.util.function.Consumer;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.RichTextChange;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.mgr.WindowManager;
import de.sos.rcp.ui.IEditor;
import de.sos.rcp.ui.editor.AbstractFileEditor;
import de.sos.rcp.ui.editor.EditorFileAssociation;
import javafx.scene.Node;
import javafx.stage.Stage;

public class TextFileEditor extends AbstractFileEditor implements IEditor {

	public static void register(){
		RCPApplication.getInstance();
		register(RCPApplication.getWindowManager());
	}
	
	public static void register(WindowManager windowManager) {
		windowManager.registerEditor(TextFileEditor.class, "TextFile", "General");
		EditorFileAssociation.register("TextFile", "txt", "java", "xml", "emod", "layout");
	}

	public TextFileEditor(){
		this(null);
	}
	public TextFileEditor(File file) {
		super(file, "TextFile");
	}

	CodeArea		mCodeArea = null;
	private boolean	mFirst = true;
	
	@Override
	public Node getFXNode(Stage mPrimaryStage) {
		if (mCodeArea == null){
			mCodeArea = new CodeArea();
			mCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(mCodeArea));
			Consumer<RichTextChange<Collection<String>, Collection<String>>> a = new Consumer<RichTextChange<Collection<String>,Collection<String>>>() {				
				@Override
				public void accept(RichTextChange<Collection<String>, Collection<String>> t) {
					if (mFirst){
						mFirst = false;
					}else
						markDirty();					
				}
			};
			mCodeArea.richChanges().successionEnds(Duration.ofMillis(500)).subscribe(a );
		}
		return mCodeArea;
	}
	
	

	
	@Override
	protected void doLoad() {
		if (mCodeArea == null) getFXNode(null);
		try {
			Scanner scanner = new Scanner(getFile());
			String txt = scanner.useDelimiter("\\Z").next();
			mCodeArea.replaceText(txt);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doSave() {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(getFile()));
			bw.write(mCodeArea.getText());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	
	

}
