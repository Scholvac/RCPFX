package de.sos.rcp.ui.views.general.properties;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanProperty;

import com.anchorage.docks.node.DockNode.DockPosition;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.mgr.WindowManager;
import de.sos.rcp.mgr.selection.Selection;
import de.sos.rcp.ui.IView;
import de.sos.rcp.ui.impl.AbstractView;
import javafx.scene.Node;
import javafx.stage.Stage;

public class PropertiesView extends AbstractView implements IView, Consumer<Selection> {

	private PropertySheet mPropertySheet;

	static void register(){
		RCPApplication.getInstance();
		register(RCPApplication.getWindowManager());
	}
	
	public static void register(WindowManager windowManager) {
		windowManager.registerView(PropertiesView.class, "Properties", "General", DockPosition.RIGHT);		
	}

	public PropertiesView() {
		super("Properties", "Properties");
	}
	

	@Override
	public Node getFXNode(Stage mPrimaryStage) {
		if (mPropertySheet == null){
			mPropertySheet = new PropertySheet();
			RCPApplication.getSelectionManager().successionEnds(Duration.ofMillis(50)).subscribe(this);
		}
		return mPropertySheet;
	}
	
	protected PropertySheet getPropertySheet(){
		return mPropertySheet;
	}
	
	@Override
	public void onClose() {
		super.onClose();
		RCPApplication.getSelectionManager().removeObserver(this);
	}

	@Override
	public void accept(Selection t) {
		RCPLog.trace("Receive Selection: " + t);
		if (mPropertySheet != null){
			mPropertySheet.getItems().clear();
			if (t != null && t.getValues() != null){
				ArrayList<Item> out = new ArrayList<Item>();
				for (Object obj : t.getValues()){
					Collection<Item> items = createItems(obj);
					if (items != null && items.isEmpty() == false)
						out.addAll(items);
				}
				mPropertySheet.getItems().addAll(out);
			}
		}
		
	}

	protected Collection<Item> createItems(Object obj) {
		try {
			BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] descriptors = bi.getPropertyDescriptors();
			if (descriptors != null && descriptors.length != 0){
				ArrayList<Item> out = new ArrayList<PropertySheet.Item>();
				for (PropertyDescriptor pd : descriptors)
					out.add(new BeanProperty(obj, pd));
				return out;
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
