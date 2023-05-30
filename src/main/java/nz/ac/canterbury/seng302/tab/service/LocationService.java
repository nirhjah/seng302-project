package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    /**
     * Gets all locations stored in the database.
     * @return a list of all locations in the database
     */
    public List<Location> getLocationList() {
        return locationRepository.findAll();
    }

    /**
     * Adds a location to the database
     * @param location location to be saved
     * @return the updated location after saving
     */
    public Location addLocation(Location location) {
        return locationRepository.save(location);
    }
}
