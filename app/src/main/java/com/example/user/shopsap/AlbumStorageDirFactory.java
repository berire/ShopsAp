package com.example.user.shopsap;

/**
 * Created by user on 30.10.2017.
 */

import java.io.File;

abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
