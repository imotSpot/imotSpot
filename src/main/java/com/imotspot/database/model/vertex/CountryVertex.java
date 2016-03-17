package com.imotspot.database.model.vertex;

import com.imotspot.dashboard.domain.imot.Country;
import com.imotspot.database.model.core.WithNameVertex;

public class CountryVertex extends WithNameVertex {

    public CountryVertex(Country country) {
        super(country);
    }

}
