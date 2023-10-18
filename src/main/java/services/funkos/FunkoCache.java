package services.funkos;

import models.Funko;
import services.cache.Cache;

interface FunkoCache extends Cache<Long, Funko> {
}
