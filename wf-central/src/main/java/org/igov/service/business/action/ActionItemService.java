/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.igov.io.GeneralConfig;
import org.igov.model.action.item.Category;
import org.igov.model.action.item.Service;
import org.igov.model.action.item.ServiceData;
import org.igov.model.action.item.Subcategory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.core.Entity;
import org.igov.service.business.object.place.KOATUU;
import org.igov.model.object.place.Place;
import org.igov.model.object.place.PlaceDao;
import static org.igov.util.Tool.bFoundText;
import org.igov.util.cache.CachedInvocationBean;
import org.igov.util.cache.MethodCacheInterceptor;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.cache.SerializableResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ActionItemService {

    public static final String SERVICE_NAME_TEST_PREFIX = "_";
    public static final List<String> SUPPORTED_PLACE_IDS = new ArrayList<>();
    public static final String GET_SERVICES_TREE = "getServicesTree";

    static {
        SUPPORTED_PLACE_IDS.add(String.valueOf(KOATUU.KYIVSKA_OBLAST.getId()));
        SUPPORTED_PLACE_IDS.add(String.valueOf(KOATUU.KYIV.getId()));
    }

    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    private BaseEntityDao baseEntityDao;
    //@Autowired
    //private EntityService entityService;
    //@Autowired
    //private TableDataService tableDataService;
    //@Autowired
    //private CachedInvocationBean cachedInvocationBean;
    @Autowired(required = false)
    private MethodCacheInterceptor methodCacheInterceptor;
    @Autowired
    private PlaceDao placeDao;

    public SerializableResponseEntity<String> categoriesToJsonResponse(List<Category> categories) {
        for (Category c : categories) {
            for (Subcategory sc : c.getSubcategories()) {
                sc.setCategory(null);

                for (Service service : sc.getServices()) {
                    service.setFaq(null);
                    service.setInfo(null);
                    service.setLaw(null);
                    //service.setSub(service.getServiceDataList().size());

                    List<ServiceData> serviceDataFiltered = service.getServiceDataFiltered(generalConfig.bTest());
                    service.setSub(serviceDataFiltered != null ? serviceDataFiltered.size() : 0);
                    //service.setTests(service.getTestsCount());
                    //service.setStatus(service.getTests(); service.getTestsCount());
                    service.setStatus(service.getStatusID());
                    service.setServiceDataList(null);
                    service.setSubcategory(null);
                }
            }
        }

        return new SerializableResponseEntity<>(JsonRestUtils.toJsonResponse(categories));
    }

    public void filterOutServicesByServiceNamePrefix(List<Category> aCategory, String sPrefix) {
        for (Category oCategory : aCategory) {
            for (Subcategory oSubcategory : oCategory.getSubcategories()) {
                for (Iterator<Service> oServiceIterator = oSubcategory.getServices().iterator(); oServiceIterator
                        .hasNext();) {
                    Service oService = oServiceIterator.next();
                    if (oService.getName().startsWith(sPrefix)) {
                        oServiceIterator.remove();
                    }
                }
            }
        }
    }

    public void filterServicesByServiceName(List<Category> aCategory, String sFind) {
        for (Category oCategory : aCategory) {
            for (Subcategory oSubcategory : oCategory.getSubcategories()) {
                for (Iterator<Service> oServiceIterator = oSubcategory.getServices().iterator(); oServiceIterator
                        .hasNext();) {
                    Service oService = oServiceIterator.next();
                    if (!bFoundText(oService.getName(), sFind)) {
                        oServiceIterator.remove();
                    }
                }
            }
        }
    }

    public static boolean checkIdPlacesContainsIdUA(PlaceDao placeDao, Place place, List<String> asID_Place_UA) {
        boolean res = false;

        if (place != null) {
            if (asID_Place_UA.contains(place.getsID_UA())) {
                res = true;
            } else {
                Place root = placeDao.getRoot(place);

                if (root != null && asID_Place_UA.contains(root.getsID_UA())) {
                    res = true;
                }
            }
        }

        return res;
    }

    public void filterServicesByPlaceIds(List<Category> aCategory, List<String> asID_Place_UA) {
        Set<Place> matchedPlaces = new HashSet<>(); // cache for optimization purposes

        for (Category oCategory : aCategory) {
            for (Subcategory oSubcategory : oCategory.getSubcategories()) {
                filterSubcategoryByPlaceIds(asID_Place_UA, oSubcategory, matchedPlaces);
            }
        }
    }

    public void filterSubcategoryByPlaceIds(List<String> asID_Place_UA, Subcategory oSubcategory,
            Set<Place> matchedPlaces) {
        for (Iterator<Service> oServiceIterator = oSubcategory.getServices().iterator(); oServiceIterator.hasNext();) {
            Service oService = oServiceIterator.next();
            boolean serviceMatchedToIds = false;
            boolean nationalService = false;

            //List<ServiceData> serviceDatas = service.getServiceDataFiltered(generalConfig.bTest());
            List<ServiceData> aServiceData = oService.getServiceDataFiltered(true);
            if (aServiceData != null) {
                for (Iterator<ServiceData> oServiceDataIterator = aServiceData.iterator(); oServiceDataIterator
                        .hasNext();) {
                    ServiceData serviceData = oServiceDataIterator.next();

                    Place place = serviceData.getoPlace();

                    boolean serviceDataMatchedToIds = false;
                    if (place == null) {
                        nationalService = true;
                        continue;
                    }

                    serviceDataMatchedToIds = matchedPlaces.contains(place);

                    if (!serviceDataMatchedToIds) {
                        // heavy check because of additional queries
                        serviceDataMatchedToIds = checkIdPlacesContainsIdUA(placeDao, place, asID_Place_UA);
                    }

                    if (serviceDataMatchedToIds) {
                        matchedPlaces.add(place);
                        serviceMatchedToIds = true;
                        continue;
                    }

                    oServiceDataIterator.remove();
                }
            }
            if (!serviceMatchedToIds && !nationalService) {
                oServiceIterator.remove();
            } else {
                oService.setServiceDataList(aServiceData);
            }
        }
    }

    /**
     * Filter out empty categories and subcategories
     *
     * @param aCategory
     */
    public void hideEmptyFolders(List<Category> aCategory) {
        for (Iterator<Category> oCategoryIterator = aCategory.iterator(); oCategoryIterator.hasNext();) {
            Category oCategory = oCategoryIterator.next();

            for (Iterator<Subcategory> oSubcategoryIterator = oCategory.getSubcategories().iterator(); oSubcategoryIterator
                    .hasNext();) {
                Subcategory oSubcategory = oSubcategoryIterator.next();
                if (oSubcategory.getServices().isEmpty()) {
                    oSubcategoryIterator.remove();
                }
            }

            if (oCategory.getSubcategories().isEmpty()) {
                oCategoryIterator.remove();
            }
        }
    }

    public <T extends Entity> ResponseEntity deleteApropriateEntity(T entity) {
        baseEntityDao.delete(entity);
        return JsonRestUtils.toJsonResponse(HttpStatus.OK, "success", entity.getClass() + " id: " + entity.getId() + " removed");
    }

    public <T extends Entity> ResponseEntity recursiveForceServiceDelete(Class<T> entityClass, Long nID) {
        T entity = baseEntityDao.findById(entityClass, nID);
        // hibernate will handle recursive deletion of all child entities
        // because of annotation: @OneToMany(mappedBy = "category",cascade = CascadeType.ALL, orphanRemoval = true)
        baseEntityDao.delete(entity);
        return JsonRestUtils.toJsonResponse(HttpStatus.OK, "success", entityClass + " id: " + nID + " removed");
    }

    public ResponseEntity regionsToJsonResponse(Service oService) {
        oService.setSubcategory(null);

        List<ServiceData> aServiceData = oService.getServiceDataFiltered(generalConfig.bTest());
        for (ServiceData oServiceData : aServiceData) {
            oServiceData.setService(null);

            Place place = oServiceData.getoPlace();
            if (place != null) {
                // emulate for client that oPlace contain city and oPlaceRoot contain oblast

                Place root = placeDao.getRoot(place);
                oServiceData.setoPlaceRoot(root);
                /* убрано чтоб не создавать нестандартност
                 if (PlaceTypeCode.OBLAST == place.getPlaceTypeCode()) {
                 oServiceData.setoPlace(null);   // oblast can't has a place
                 }
                 }*/
            }

            // TODO remove if below after migration to new approach (via Place)
            if (oServiceData.getCity() != null) {
                oServiceData.getCity().getRegion().setCities(null);
            } else if (oServiceData.getRegion() != null) {
                oServiceData.getRegion().setCities(null);
            }
        }

        oService.setServiceDataList(aServiceData);
        return JsonRestUtils.toJsonResponse(oService);
    }

    public <T extends Entity> ResponseEntity deleteEmptyContentEntity(Class<T> entityClass, Long nID) {
        T entity = baseEntityDao.findById(entityClass, nID);
        if (entity.getClass() == Service.class) {
            if (((Service) entity).getServiceDataList().isEmpty()) {
                return deleteApropriateEntity(entity);
            }
        } else if (entity.getClass() == Subcategory.class) {
            if (((Subcategory) entity).getServices().isEmpty()) {
                return deleteApropriateEntity(entity);
            }
        } else if (entity.getClass() == Category.class) {
            if (((Category) entity).getSubcategories().isEmpty()) {
                return deleteApropriateEntity(entity);
            }
        } else if (entity.getClass() == ServiceData.class) {
            return deleteApropriateEntity(entity);
        }
        return JsonRestUtils.toJsonResponse(HttpStatus.NOT_MODIFIED, "error", "Entity isn't empty");
    }

    public ResponseEntity tryClearGetServicesCache(ResponseEntity oResponseEntity) {
        if (methodCacheInterceptor != null && HttpStatus.OK.equals(oResponseEntity.getStatusCode())) {
            methodCacheInterceptor
                    .clearCacheForMethod(CachedInvocationBean.class, "invokeUsingCache", GET_SERVICES_TREE);
        }

        return oResponseEntity;
    }

}
