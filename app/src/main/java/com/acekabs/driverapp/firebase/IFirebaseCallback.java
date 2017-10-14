package com.acekabs.driverapp.firebase;

/**
 * Interface to pass data fetched from firebase DB as a callback to
 * the requestor.
 */

public interface IFirebaseCallback<T> {
    public void onDataReceived(T data);

    public void onFailure(Exception ex);
}
