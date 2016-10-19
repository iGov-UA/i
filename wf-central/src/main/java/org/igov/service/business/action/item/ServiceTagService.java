package org.igov.service.business.action.item;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.model.action.item.*;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.object.place.Place;
import org.igov.service.controller.ActionItemController;
import org.igov.util.cache.CachedInvocationBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: goodg_000
 * Date: 10.07.2016
 * Time: 16:44
 */
@org.springframework.stereotype.Service
public class ServiceTagService {
    private static final long FAKE_ROOT_TAG_ID  = 0;
    private static final String GET_SERVICE_TAG_TREE_CACHE_KEY = "ServiceTagService.getServiceTagTree";
    private static final String GET_TAG_ID_TO_SERVICES_CACHE_KEY = "ServiceTagService.getTagIdToServicesMap";

    @Autowired
    private BaseEntityDao<Long> baseEntityDao;

    @Autowired
    private CachedInvocationBean cachedInvocationBean;

    @Autowired
    GeneralConfig generalConfig;

    public List<ServiceTagTreeNodeVO> getCatalogTreeTag(Long nID_Category, String sFind,
                                                        List<String> asID_Place_UA, boolean bShowEmptyFolders,
                                                        boolean includeServices,
                                                        Long nID_ServiceTag_Root, Long nID_ServiceTag_Child) {
        List<ServiceTagTreeNodeVO> res = new ArrayList<>();
        boolean includeTestEntities = generalConfig.isSelfTest();

        boolean hasRootIdFilter = nID_ServiceTag_Root != null;
        boolean hasChildIdFilter = nID_ServiceTag_Child != null;

        ServiceTagTree tree = getServiceTagTreeCached(includeTestEntities);
        Map<Long, List<Service>> tagIdToServices = getTagIdToServicesMapCached(includeTestEntities);

        for (ServiceTagTreeNode rootTagNode : tree.getRootTagNodes()) {
            final ServiceTag parentTag = rootTagNode.getTag();

            final Long rootTagId = rootTagNode.getTag().getId();
            if (hasRootIdFilter && !rootTagId.equals(nID_ServiceTag_Root)) {
                continue;
            }

            ServiceTagTreeNodeVO nodeVO = new ServiceTagTreeNodeVO();
            nodeVO.setoServiceTag_Root(parentTag);
            for (ServiceTagTreeNode childNode : rootTagNode.getChildren()) {
                final ServiceTag childTag = childNode.getTag();

                if (hasChildIdFilter && !childNode.getTag().getId().equals(nID_ServiceTag_Child)) {
                    continue;
                }

                if (!isSuitable(parentTag, tagIdToServices.get(childTag.getId()), nID_Category, sFind, asID_Place_UA)) {
                    continue;
                }

                nodeVO.addChild(childTag);
            }

            if (nodeVO.getaServiceTag_Child().isEmpty() &&
                    !isSuitable(parentTag, tagIdToServices.get(parentTag.getId()), nID_Category, sFind,
                            asID_Place_UA)) {
                continue;
            }

            if (!nodeVO.getaServiceTag_Child().isEmpty() || bShowEmptyFolders) {
                res.add(nodeVO);

                if (!includeServices) {
                    continue;
                }

                Stream<Service> servicesStream = nodeVO.getaServiceTag_Child().stream().flatMap(
                        c -> aService(tagIdToServices, c.getId()).stream());
                if (!hasChildIdFilter) {
                    servicesStream = Stream.concat(servicesStream, aService(tagIdToServices, rootTagId).stream());
                }

                final List<Service> selectedServices = servicesStream
                        .distinct().filter(s -> isSuitable(s, nID_Category, sFind, asID_Place_UA))
                        .collect(Collectors.toList());

                nodeVO.setaService(selectedServices);
            }
        }

        return res;
    }

    private List<Service> aService(Map<Long, List<Service>> tagIdToServicesMap, Long tagId) {
        List<Service> aService = tagIdToServicesMap.get(tagId);
        if (aService == null) {
            aService = new ArrayList<>();
        }
        return aService;
    }
    
    private boolean isSuitable(ServiceTag serviceTag, List<Service> services,
                               Long nID_Category, String sFind, List<String> asID_Place_UA) {
        if (CollectionUtils.isEmpty(services)) {
            return false;
        }

        String sFindForServices = sFind;
        if (StringUtils.isNotBlank(sFind)) {
            if (containsWithoutCase(serviceTag.getsID(), sFind) ||
                    containsWithoutCase(serviceTag.getsName_UA(), sFind) ||
                    containsWithoutCase(serviceTag.getsName_RU(), sFind)) {

                sFindForServices = null;
            }
        }

        boolean res = false;
        for (Service service : services) {
            if (isSuitable(service, nID_Category, sFindForServices, asID_Place_UA)) {
                res = true;
                break;
            }
        }

        return res;
    }

    private boolean isSuitable(Service service,
                               Long nID_Category, String sFind, List<String> asID_Place_UA) {
        boolean res = true;
        if (nID_Category != null) {
            res = nID_Category.equals(service.getSubcategory().getCategory().getId());
        }

        if (res && StringUtils.isNotBlank(sFind)) {
            res = containsWithoutCase(service.getName(), sFind.toLowerCase());
        }
        if (res && CollectionUtils.isNotEmpty(asID_Place_UA)) {
            Set<String> placesSet = new HashSet<>(asID_Place_UA);

            boolean placeFound = false;
            for (ServiceData serviceData : service.getServiceDataList()) {
                final Place place = serviceData.getoPlace();
                if (place == null || placesSet.contains(place.getsID_UA())) {
                    placeFound = true;
                    break;
                }
            }
            res = placeFound;
        }
        return res;
    }

    private boolean containsWithoutCase(String source, String target) {
        return source != null && source.toLowerCase().contains(target.toLowerCase());
    }

    private ServiceTagTree getServiceTagTreeCached(boolean includeTestEntities) {
        return cachedInvocationBean.invokeUsingCache(new CachedInvocationBean.Callback<ServiceTagTree>(
                GET_SERVICE_TAG_TREE_CACHE_KEY, includeTestEntities) {
            @Override
            public ServiceTagTree execute() {
                return getServiceTagTree(includeTestEntities);
            }
        });
    }

    private ServiceTagTree getServiceTagTree(boolean includeTestEntities) {
        List<ServiceTagRelation> relations = new ArrayList<>(baseEntityDao.findAll(ServiceTagRelation.class));
        Map<ServiceTag, ServiceTagTreeNode> tagToNodeMap = new HashMap<>();

        Set<ServiceTag> parentTags = new LinkedHashSet<>();
        Set<ServiceTag> childTags = new HashSet<>();

        for (ServiceTagRelation relation : relations) {
            final ServiceTag parent = relation.getServiceTag_Parent();
            final ServiceTag child = relation.getServiceTag_Child();

            if (isExcludeTestEntity(includeTestEntities, parent) || isExcludeTestEntity(includeTestEntities, child)) {
                continue;
            }

            ServiceTagTreeNode parentNode = null;
            if (parent.getId() != FAKE_ROOT_TAG_ID) {
                parentNode = tagToNodeMap.get(parent);
                if (parentNode == null) {
                    parentTags.add(parent);
                    parentNode = new ServiceTagTreeNode(parent);
                    tagToNodeMap.put(parent, parentNode);
                }
            }

            ServiceTagTreeNode childNode = tagToNodeMap.get(child);
            if (childNode == null) {
                childTags.add(child);
                childNode = new ServiceTagTreeNode(child);
                tagToNodeMap.put(child, childNode);
            }

            if (parentNode != null) {
                parentNode.addChild(childNode);
            }
        }

        Set<ServiceTag> rootTags = new LinkedHashSet<>(parentTags);
        rootTags.removeAll(childTags);

        final List<ServiceTagTreeNode> rootTagNodes = rootTags.stream().map(tagToNodeMap::get).collect(
                Collectors.toList());
        return new ServiceTagTree(rootTagNodes);
    }

    private boolean isExcludeTestEntity(boolean includeTestEntity, ServiceTag serviceTag) {
        return isExcludeTestEntity(includeTestEntity, serviceTag.getsID());
    }

    private Map<Long, List<Service>> getTagIdToServicesMapCached(boolean includeTestEntities) {
        return cachedInvocationBean.invokeUsingCache(new CachedInvocationBean.Callback<Map<Long, List<Service>>>(
                GET_TAG_ID_TO_SERVICES_CACHE_KEY, includeTestEntities) {
            @Override
            public Map<Long, List<Service>> execute() {
                return getTagIdToServicesMap(includeTestEntities);
            }
        });
    }

    private Map<Long, List<Service>> getTagIdToServicesMap(boolean includeTestEntities) {
        Map<Long, List<Service>> res = new HashMap<>();

        List<ServiceTagLink> links = new ArrayList<>(baseEntityDao.findAll(ServiceTagLink.class));
        for (ServiceTagLink link : links) {
            final ServiceTag serviceTag = link.getServiceTag();
            final Service service = link.getService();

            if (isExcludeTestEntity(includeTestEntities, serviceTag) ||
                    isExcludeTestEntity(includeTestEntities, service)) {
                continue;
            }

            final Long serviceTagId = serviceTag.getId();
            List<Service> services = res.get(serviceTagId);
            if (services == null) {
                services = new ArrayList<>();
                res.put(serviceTagId, services);
            }
            services.add(service);
        }

        return res;
    }

    private boolean isExcludeTestEntity(boolean includeTestEntity, Service service) {
        return isExcludeTestEntity(includeTestEntity, service.getName());
    }

    private boolean isExcludeTestEntity(boolean includeTestEntity, String nameOrId) {
        return !includeTestEntity && nameOrId.startsWith(ActionItemController.SERVICE_NAME_TEST_PREFIX);
    }
}
