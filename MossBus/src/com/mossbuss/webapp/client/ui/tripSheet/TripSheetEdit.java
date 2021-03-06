package com.mossbuss.webapp.client.ui.tripSheet;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.mossbuss.webapp.client.GreetingService;
import com.mossbuss.webapp.client.GreetingServiceAsync;
import com.mossbuss.webapp.client.dto.BusDTO;
import com.mossbuss.webapp.client.dto.ClientDTO;
import com.mossbuss.webapp.client.dto.DriverDTO;
import com.mossbuss.webapp.client.dto.StudentDTO;
import com.mossbuss.webapp.client.dto.TripSheetDTO;

public class TripSheetEdit extends Composite {

	private static studentEditUiBinder uiBinder = GWT
			.create(studentEditUiBinder.class);

	private TripSheetDTO tripDetails = new TripSheetDTO();
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	private ArrayList<String> Students = new ArrayList<String>();
	private ArrayList<BusDTO> Busses = new ArrayList<BusDTO>();
	private ArrayList<DriverDTO> Drivers = new ArrayList<DriverDTO>();
	private int prevDriver = 0;
	private int prevBus = 0;
	private StudentDTO studentDetails;

	@UiField
	TabPanel tPanel;
	@UiField
	TextBox tripNameField;

	@UiField
	ListBox driverSelectBox;
	@UiField
	ListBox busSelectBox;

	@UiField
	Button saveButton;
	@UiField
	Button cancelButton;
	@UiField
	Label errorLabel;

	interface studentEditUiBinder extends UiBinder<Widget, TripSheetEdit> {
	}

	public TripSheetEdit() {
		initWidget(uiBinder.createAndBindUi(this));
		// Create Customer Edit Interface based on CustomerDTO
		tPanel.selectTab(0);

	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public ListBox getBusSelectBox() {
		return busSelectBox;
	}

	public ListBox getDriverSelectBox() {
		return driverSelectBox;
	}

	public void driverInit() {
		System.out.println("AT INIT DOING SOMETHING HERE XXXXXXXXXXXX");
		greetingService.getAllDrivers(tripDetails.getID(),
				new AsyncCallback<ArrayList<DriverDTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Fix This:
						// errorLabel.setText(caught.getMessage());
						System.out.println("Got to failure.!!!!");
					}

					@Override
					public void onSuccess(ArrayList<DriverDTO> result) {
						Drivers.clear();
						driverSelectBox.clear();
						System.out.println("Got to Success");
						for (int i = 0; i < result.size(); i++) {
							DriverDTO newDriver = new DriverDTO();

							newDriver.setBusID(result.get(i).getBusID());
							newDriver.setTripSheetID(result.get(i)
									.getTripSheetID());
							newDriver.setID(result.get(i).getID());
							newDriver.setAdmin(result.get(i).getAdmin());
							newDriver.setName(result.get(i).getName());
							newDriver.setPassword(result.get(i).getPassword());
							newDriver.setEmailAddress(result.get(i)
									.getEmailAddress());
							Drivers.add(newDriver);

						}
						System.out.println("DriverArray LIST SIZE IS : "
								+ Drivers.size());
						System.out.println("DriverArray LIS XXXX T SIZE IS : "
								+ Drivers.size());
						System.out.println("result LIST SIZE IS : "
								+ result.size());
						if (Drivers.size() > 0) {
							if (tripDetails.getDriverID() == Drivers.get(0)
									.getID()) {
								driverSelectBox.addItem(Drivers.get(0)
										.getName());
								prevDriver = Drivers.get(0).getID();
								for (int i = 1; i < Drivers.size(); i++) {

									if (Drivers.get(i).getTripSheetID() < 0) {
										driverSelectBox.addItem(Drivers.get(i)
												.getName());

									} else {
										System.out
												.println("REMOVING FROM DRIVERS "
														+ Drivers
																.get(i)
																.getTripSheetID());
										Drivers.remove(i);

										i--;

									}
								}
							} else {
								for (int i = 0; i < Drivers.size(); i++) {

									if (Drivers.get(i).getTripSheetID() < 0) {
										driverSelectBox.addItem(Drivers.get(i)
												.getName());

									} else {
										System.out
												.println("REMOVING FROM DRIVERS "
														+ Drivers
																.get(i)
																.getTripSheetID());
										System.out
												.println("REMOVING FROM DRIVERS");
										Drivers.remove(i);

										i--;

									}
								}
							}
						}

						// check for drivers not yet allocated to trip sheet.

					}
				});
		driverSelectBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				System.out
						.println("XXXXXXXXXXXXXXXXXXXXGOT INTO CHANGE HANDLER");

				int x = getPrevDriverID();
				if (x == 0) {

				} else {
					greetingService.getDriver(x,
							new AsyncCallback<DriverDTO>() {

								@Override
								public void onFailure(Throwable caught) {
									errorLabel.setText(caught.getMessage());
								}

								@Override
								public void onSuccess(DriverDTO result) {
									result.setTripSheetID(-1);

									// change the busses driver id to -1. it has
									// been deallocated
									if (result.getBusID() > 0) {
										greetingService.getBus(
												result.getBusID(),
												new AsyncCallback<BusDTO>() {

													@Override
													public void onFailure(
															Throwable caught) {
														errorLabel.setText(caught
																.getMessage());
													}

													@Override
													public void onSuccess(
															BusDTO result2) {

														result2.setDriverID(-1);
														greetingService
																.saveBus(
																		result2,
																		new AsyncCallback<Void>() {

																			@Override
																			public void onFailure(
																					Throwable caught) {
																				errorLabel
																						.setText(caught
																								.getMessage());
																			}

																			@Override
																			public void onSuccess(
																					Void x) {

																			}
																		});

													}
												});
									}

									result.setBusID(-1);
									greetingService.saveDriver(result,
											new AsyncCallback<Void>() {

												@Override
												public void onFailure(
														Throwable caught) {
													errorLabel.setText(caught
															.getMessage());
												}

												@Override
												public void onSuccess(Void x) {

												}
											});

								}
							});
				}
				setPrevDriverIntID(getDriverSelection().getID());
				System.out.println("new prevDriverInt = "
						+ getDriverSelection().getID());
			}
		});

	}

	private void setDrivers(ArrayList<DriverDTO> array) {
		this.Drivers = array;
	}

	private void setBusses(ArrayList<BusDTO> array) {
		this.Busses = array;
	}

	public void busInit() {
		System.out.println("GOT INTO BUSINIT");
		greetingService.getAllBusses(tripDetails.getID(),
				new AsyncCallback<ArrayList<BusDTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Fix This:
						// errorLabel.setText(caught.getMessage());
					}

					@Override
					public void onSuccess(ArrayList<BusDTO> busresult) {
						Busses.clear();
						busSelectBox.clear();
						for (int i = 0; i < busresult.size(); i++) {
							BusDTO newBus = new BusDTO();
							newBus.setDriverID(busresult.get(i).getDriverID());
							newBus.setBusName(busresult.get(i).getBusName());
							newBus.setTripSheetID(busresult.get(i)
									.getTripSheetID());
							newBus.setID(busresult.get(i).getID());
							Busses.add(newBus);

						}

						System.out.println("ONSUCCESS" + Busses.size());
						// check for busses that havent yet been allocated.

						if (Busses.size() > 0) {
							if (tripDetails.getBusID() == Busses.get(0).getID()) {
								busSelectBox
										.addItem(Busses.get(0).getBusName());
								prevBus = Busses.get(0).getID();
								for (int i = 1; i < Busses.size(); i++) {
									if (Busses.get(i).getTripSheetID() < 0) {
										busSelectBox.addItem(Busses.get(i)
												.getBusName());
									} else {
										Busses.remove(i);
										i--;
									}
								}
							} else {
								for (int i = 0; i < Busses.size(); i++) {
									if (Busses.get(i).getTripSheetID() < 0) {
										busSelectBox.addItem(Busses.get(i)
												.getBusName());
									} else {
										Busses.remove(i);
										i--;
									}
								}
							}
						}

					}
				});

		busSelectBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				int x = getPrevBusID();
				if (x == 0) {

				} else {
					greetingService.getBus(x, new AsyncCallback<BusDTO>() {

						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(caught.getMessage());
						}

						@Override
						public void onSuccess(BusDTO result) {
							result.setTripSheetID(-1);
							// make sure Drivers BusID is changed as well
							if (result.getDriverID() > 0) {
								greetingService.getDriver(result.getDriverID(),
										new AsyncCallback<DriverDTO>() {

											@Override
											public void onFailure(
													Throwable caught) {
												errorLabel.setText(caught
														.getMessage());
											}

											@Override
											public void onSuccess(
													DriverDTO result2) {

												result2.setBusID(-1);
												greetingService
														.saveDriver(
																result2,
																new AsyncCallback<Void>() {

																	@Override
																	public void onFailure(
																			Throwable caught) {
																		errorLabel
																				.setText(caught
																						.getMessage());
																	}

																	@Override
																	public void onSuccess(
																			Void x) {

																	}
																});

											}
										});
							}

							result.setDriverID(-1);
							greetingService.saveBus(result,
									new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											errorLabel.setText(caught
													.getMessage());
										}

										@Override
										public void onSuccess(Void x) {

										}
									});

						}
					});
				}
				setPrevBusIntID(getBusSelection().getID());
				System.out.println("new prevBusInt = "
						+ getBusSelection().getID());
			}
		});
	}

	public void setPrevBusIntID(int newID) {
		prevBus = newID;
	}

	public void setPrevDriverIntID(int newID) {
		prevDriver = newID;
	}

	public TripSheetDTO getTripSheet() {
		TripSheetDTO trip = new TripSheetDTO();
		trip.setTripName(tripNameField.getText());
		trip.setDriverID(getDriverSelection().getID());
		trip.setID(tripDetails.getID());
		trip.setBusID(getBusSelection().getID());
		System.out.println("returned id is : " + getBusSelection().getID());
		trip.setDriverName(getDriverSelection().getName());

		return trip;
	}

	public int getPrevDriverID() {
		return prevDriver;
	}

	public int getPrevBusID() {
		return prevBus;
	}

	public void setTripSheet(TripSheetDTO tripSheet) {
		this.tripDetails = tripSheet;

	}

	public void initTripSheet() {
		tripNameField.setText(tripDetails.getTripName());
		driverInit();
		busInit();
		System.out.println("JUST BEFORE ERROR DRIVER INT SELECTION IS : "
				+ getDriverListBoxPos() + " |Driver Name is: "
				+ getDriverSelection().getName());
		if (this.driverSelectBox.getItemCount() > 0) {
			this.driverSelectBox.setItemSelected(0, true);
		}
		if (this.busSelectBox.getItemCount() > 0) {
			this.busSelectBox.setItemSelected(0, true);
		}
	}

	public void selectTpanel(int selector) {
		tPanel.selectTab(selector);
	}

	public int getDriverListBoxPos() {

		int counter = 0;
		System.out
				.println("_______________________________________________________________________________"
						+ Drivers.size());
		for (int i = 0; i < Drivers.size(); i++) {
			System.out.println("_________________ tripDetails.getDriverID = "
					+ tripDetails.getDriverID() + "| Drivers.get(i).getID() = "
					+ Drivers.get(i).getID());
			if (tripDetails.getDriverID() == Drivers.get(i).getID()) {
				return i;
			}
		}
		// this should never happen;
		return 0;
	}

	public int getBoxListBoxPos() {

		int counter = 0;

		for (int i = 0; i < Drivers.size(); i++) {
			if (tripDetails.getBusID() == Busses.get(i).getID()) {
				return i;
			}
		}
		// this should never happen;
		return 0;
	}

	public int getDriverIntSelection() {
		int Item = driverSelectBox.getSelectedIndex();
		int counter = 0;

		for (int i = 0; i < Drivers.size(); i++) {
			if (Drivers.get(i).getTripSheetID() < 0
					|| tripDetails.getDriverID() == Drivers.get(i).getID()) {
				counter++;
				if (counter == Item) {
					return counter;
				}
			}
		}
		// this should never happen;
		return 0;
	}

	public int getBusIntSelection() {
		int Item = busSelectBox.getSelectedIndex();
		int counter = 0;

		for (int i = 0; i < Busses.size(); i++) {
			System.out.println("XXXXXX in getBusIntSelection " + Busses.size());
			if (Busses.get(i).getTripSheetID() < 0
					|| tripDetails.getBusID() == Busses.get(i).getID()) {

				if (counter == Item) {
					return counter;
				}
				counter++;
			}
		}
		// this should never happen;
		return 0;
	}

	public DriverDTO getDriverSelection() {
		int Item = driverSelectBox.getSelectedIndex();
		int counter = 0;
		DriverDTO selectedDriver = new DriverDTO();

		for (int i = 0; i < Drivers.size(); i++) {

			if (Drivers.get(i).getTripSheetID() < 0
					|| tripDetails.getDriverID() == Drivers.get(i).getID()) {

				if (counter == Item) {
					selectedDriver = Drivers.get(i);
					i = Drivers.size();
				}
				counter++;
			}
		}
		return selectedDriver;
	}

	public BusDTO getBusSelection() {
		int Item = busSelectBox.getSelectedIndex();
		int counter = 0;
		BusDTO selectedBus = new BusDTO();
		for (int i = 0; i < Busses.size(); i++) {
			System.out.println("XXXXXX in getBusSelection " + Busses.size());
			if (Busses.get(i).getTripSheetID() < 0
					|| tripDetails.getBusID() == Busses.get(i).getID()) {
				if (counter == Item) {
					selectedBus = Busses.get(i);
					i = Busses.size();
				}
				counter++;
			}
		}
		return selectedBus;
	}

}
