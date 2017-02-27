package com.oraclecorp.internal.ent3.NAS.publicsector1;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import oracle.adf.model.datacontrols.device.DeviceManagerFactory;
import oracle.adfmf.amx.event.MapBoundsChangeEvent;
import oracle.adfmf.amx.event.MapInputEvent;
import oracle.adfmf.framework.api.AdfmfJavaUtilities;
import oracle.adfmf.framework.exception.AdfException;
import oracle.adfmf.java.beans.PropertyChangeListener;
import oracle.adfmf.java.beans.PropertyChangeSupport;
import oracle.adfmf.java.beans.ProviderChangeSupport;

public class Location {
	private Date time;
	private double latitude;
	private double longitude;
	private String name;
	private String type;

	public Location() {
		home();
	}

	private List<Location> locations = new LinkedList<Location>();

	private static final String LATITUDE = "#{preferenceScope.application.location.latitude}";
	private static final String LONGITUDE = "#{preferenceScope.application.location.longitude}";
	private static final String NAME = "#{preferenceScope.application.location.name}";
	private static final String TYPE = "#{preferenceScope.application.location.type}";
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ProviderChangeSupport providerChangeSupport = new ProviderChangeSupport(this);

	public Location(double latitude, double longitude, String name, String type) {
		update(latitude, longitude, name, type);
	}

	public Location[] getLocations() {
		return (Location[]) locations.toArray(new Location[] {});
	}

	/** commandButton action */
	public void home() {
		Console.info("Welcome");
		// from preferences
		update(Double.parseDouble((String) AdfmfJavaUtilities.getELValue(LATITUDE)),
				Double.parseDouble((String) AdfmfJavaUtilities.getELValue(LONGITUDE)),
				(String) AdfmfJavaUtilities.getELValue(NAME),
				(String) AdfmfJavaUtilities.getELValue(TYPE));
		// TODO pref: Add default location to saved locations?
	}

	public void save() {
		Console.info("Save " + name);
		boolean removed = locations.removeIf(item -> item.is(latitude, longitude));
		if ("".contentEquals(name))
			name = latitude + ", " + longitude;
		locations.add(new Location(latitude, longitude, name, type));
		propertyChangeSupport.firePropertyChange("locations", new Location[] {}, locations);
		providerChangeSupport.fireProviderRefresh("locations");
		update();
		AdfmfJavaUtilities.flushDataChangeEvent();
		Console.info((removed ? "Updated " : "Added ") + name);
	}

	private boolean is(double latitude, double longitude) {
		boolean same = (latitude == this.latitude) && (longitude == this.longitude);
		Console.info(same ? "same" : "new");
		return same;
	}

	/** commandButton action fa-location-arrow GPS */
	public void getCurrentPosition() {
		// debounce
		if ((new Date().getTime() - time.getTime()) < 1000)
			return;
		Console.info("Locating");
		try {
			update(DeviceManagerFactory.getDeviceManager().getCurrentPosition(1000, true));
		} catch (AdfException e) {
			Console.error(e);
			Console.warn("No GPS");
			home();
		}
	}

	/** geographicMap mapBoundsChangeListener */
	public void change(MapBoundsChangeEvent event) {
		// debounce
		if ((new Date().getTime() - time.getTime()) < 1000)
			return;

		double x = (double) event.getCenterX();
		double y = (double) event.getCenterY();
		Console.info("move " + y + ", " + x);
		update(y, x, "", "fa-crosshairs");
	}

	/** geographicMap mapInputListener */
	public void input(MapInputEvent event) {
		// debounce
		if ((new Date().getTime() - time.getTime()) < 1000)
			return;

		double latitude = (double) event.getPointY();
		double longitude = (double) event.getPointX();
		switch (event.getType()) {
		case "mousedown":
			break;
		case "mouseup":
			break;
		case "click":
			Console.info("tap " + latitude + ", " + longitude);
			update(latitude, longitude, "", "fa-crosshairs");
			break;
		default:
			Console.warn(event.getType());
		}
		// TODO label and save location
	}

	private void update(oracle.adf.model.datacontrols.device.Location currentPosition) {
		Console.info("(from GPS)");
		update(currentPosition.getLatitude(), currentPosition.getLongitude(), "GPS", "fa-location-arrow");
	}

	// update position
	private void update(double latitude, double longitude, String name, String type) {
		Console.info(name + " " + type + " " + latitude + ", " + longitude);

		double oldLatitude = this.latitude;
		this.latitude = latitude;
		propertyChangeSupport.firePropertyChange("latitude", oldLatitude, latitude);

		double oldLongitude = this.longitude;
		this.longitude = longitude;
		propertyChangeSupport.firePropertyChange("longitude", oldLongitude, longitude);

		String oldType = this.type;
		this.type = type;
		propertyChangeSupport.firePropertyChange("type", oldType, type);

		update(name);
	}

	private void update(String name) {
		Console.info(name);

		String oldName = this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange("name", oldName, name);

		update();
	}

	// update time
	private void update() {
		Date earlier = time;
		this.time = new Date();
		Console.info(time.toString());
		AdfmfJavaUtilities.setELValue(APPLICATION_TIME, time);

		propertyChangeSupport.firePropertyChange("time", earlier, time);
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		Console.info("Renamed " + name);
		update(name);
	}

	public String getType() {
		return type;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	private static final String APPLICATION_TIME = "#{applicationScope.time}";
}
