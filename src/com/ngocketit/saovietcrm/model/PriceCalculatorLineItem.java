package com.ngocketit.saovietcrm.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PriceCalculatorLineItem implements Parcelable {
	public long id = 0;
	public String name;
	public int quantity;
	public double price;
	public double totalPrice;
	public int bogoBuy;
	public int bogoGet;
	public long productId;
	public int totalQuantity;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeInt(quantity);
		dest.writeDouble(price);
		dest.writeDouble(totalPrice);
		dest.writeInt(bogoBuy);
		dest.writeInt(bogoGet);
		dest.writeLong(productId);
		dest.writeInt(totalQuantity);
	}

	public static final Parcelable.Creator<PriceCalculatorLineItem> 
	CREATOR = new Parcelable.Creator<PriceCalculatorLineItem>() {
		public PriceCalculatorLineItem createFromParcel(Parcel in) {
			return new PriceCalculatorLineItem(in);
		}

		public PriceCalculatorLineItem[] newArray(int size) {
			return new PriceCalculatorLineItem[size];
		}
	};
	
	public PriceCalculatorLineItem() {
		
	}

	private PriceCalculatorLineItem(Parcel in) {
		id = in.readLong();
		name = in.readString();
		quantity = in.readInt();
		price = in.readDouble();
		totalPrice = in.readDouble();
		bogoBuy = in.readInt();
		bogoGet = in.readInt();
		productId = in.readLong();
		totalQuantity = in.readInt();
	}
	
	public void copy(PriceCalculatorLineItem another) {
		name = another.name;
		quantity = another.quantity;
		price = another.price;
		totalPrice = another.totalPrice;
		bogoBuy = another.bogoBuy;
		bogoGet = another.bogoGet;
		productId = another.productId;
		totalQuantity = another.totalQuantity;
	}
}
