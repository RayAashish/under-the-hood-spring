package true.service.impl;

import true.model.domain.City;
import true.service.CityService;
import org.springframework.stereotype.Service;

@Service("cityService")
public class CityServiceImpl extends BaseServiceImpl<City, Integer> implements CityService {
}