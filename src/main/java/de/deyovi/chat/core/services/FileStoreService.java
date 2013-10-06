package de.deyovi.chat.core.services;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileStoreService {

	/**
	 * Stores the given InputStream's Data into a File with the given name (or a similiar one)
	 * @param is
	 * @param name
	 * @return the Name the File eventually retrieved
	 */
	public abstract String store(InputStream is, String name);

	/**
	 * Loads a File to the OutputStream by it's name
	 * @param os
	 * @param name
	 */
	public abstract boolean load(OutputStream os, String name);
	


	/**
	 * Loads a File to the OutputStream by it's name
	 * @param name
	 */
	public abstract InputStream load(String name);

}