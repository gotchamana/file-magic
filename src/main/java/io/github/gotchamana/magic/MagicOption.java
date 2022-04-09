package io.github.gotchamana.magic;

import static io.github.gotchamana.magic.MagicLibrary.*;

import lombok.Getter;

public enum MagicOption {

    DEBUG(MAGIC_DEBUG),

    SYMLINK(MAGIC_SYMLINK),

    COMPRESS(MAGIC_COMPRESS),

    COMPRESS_TRANSP(MAGIC_COMPRESS_TRANSP),

    DEVICES(MAGIC_DEVICES),

    PRESERVE_ATIME(MAGIC_PRESERVE_ATIME);

    @Getter
    private final int value;

    private MagicOption(int value) {
        this.value = value;
    }
}
