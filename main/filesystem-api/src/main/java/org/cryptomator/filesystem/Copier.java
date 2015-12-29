/*******************************************************************************
 * Copyright (c) 2015 Sebastian Stenzel and others.
 * This file is licensed under the terms of the MIT license.
 * See the LICENSE.txt file for more info.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.filesystem;

class Copier {

	public static void copy(Folder source, Folder destination) {
		assertFoldersAreNotNested(source, destination);

		destination.delete();
		destination.create();

		source.files().forEach(sourceFile -> {
			File destinationFile = destination.file(sourceFile.name());
			copy(sourceFile, destinationFile);
		});

		source.folders().forEach(sourceFolder -> {
			Folder destinationFolder = destination.folder(sourceFolder.name());
			sourceFolder.copyTo(destinationFolder);
		});
	}

	private static void assertFoldersAreNotNested(Folder source, Folder destination) {
		if (source.isAncestorOf(destination)) {
			throw new IllegalArgumentException("Can not copy parent to child directory (src: " + source + ", dst: " + destination + ")");
		}
		if (destination.isAncestorOf(source)) {
			throw new IllegalArgumentException("Can not copy child to parent directory (src: " + source + ", dst: " + destination + ")");
		}
	}

	public static void copy(File source, File destination) {
		try (OpenFiles openFiles = DeadlockSafeFileOpener.withReadable(source).andWritable(destination).open()) {
			openFiles.readable(source).copyTo(openFiles.writable(destination));
		}
	}

}