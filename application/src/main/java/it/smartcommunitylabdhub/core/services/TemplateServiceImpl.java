package it.smartcommunitylabdhub.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.FunctionEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableTemplateService;

@Service
public class TemplateServiceImpl implements SearchableTemplateService {
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ResourcePatternResolver resourceResolver; 
	
	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	
	LoadingCache<String, List<Function>> functionCache = CacheBuilder.newBuilder()
			.expireAfterWrite(60, TimeUnit.MINUTES)
			.build(new CacheLoader<String, List<Function>>() {
				@Override
				public List<Function> load(String key) throws Exception {
					List<Function> result = new ArrayList<>();
					Resource[] resources = resourceResolver.getResources("classpath:templates/functions/*.yml");
					for(Resource resource : resources) {
						Function function = mapper.readValue(resource.getFile(), Function.class);
						result.add(function);
					}
					return result;
				}
			});
	
	private List<Function> filterFunction(Pageable pageable, FunctionEntityFilter filter) throws Exception {
		List<Function> list = functionCache.get(EntityName.FUNCTION.getValue());
		return list.stream().filter(f -> {
			boolean isOk = true;
			if(StringUtils.hasLength(filter.getName())) {
				if(StringUtils.hasLength(f.getName())) {
					isOk &= f.getName().toLowerCase().contains(filter.getName().toLowerCase());
				} else {
					isOk &= false;
				}
			}
			if(StringUtils.hasLength(filter.getKind())) {
				if(StringUtils.hasLength(f.getKind())) {
					isOk &= f.getKind().toLowerCase().equals(filter.getKind().toLowerCase());
				} else {
					isOk &= false;
				}
			}
			return isOk;
		}).collect(Collectors.toList());
	}
	
	@Override
	public Page<Function> searchFunctions(Pageable pageable, FunctionEntityFilter filter)
			throws SystemException {
		 try {
			 List<Function> list = filterFunction(pageable, filter);
			 int start = (int) pageable.getOffset();
			 int end = Math.min((start + pageable.getPageSize()), list.size());
			 List<Function> pageContent  = list.subList(start, end);
			 return new PageImpl<>(pageContent, pageable, list.size());
		} catch (Exception e) {
			throw new SystemException("error retriving templates:" + e.getMessage(), e);
		}
	}

}
