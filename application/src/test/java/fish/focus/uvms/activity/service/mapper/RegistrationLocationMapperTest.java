/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package fish.focus.uvms.activity.service.mapper;

import fish.focus.uvms.activity.service.util.MapperUtil;
import fish.focus.uvms.activity.fa.entities.RegistrationLocationEntity;
import fish.focus.uvms.activity.service.mapper.RegistrationLocationMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RegistrationLocationMapperTest {

    @Test
    public void testRegistrationLocationMapper() {
        RegistrationLocation registrationLocation = MapperUtil.getRegistrationLocation();
        RegistrationLocationMapper registrationLocationMapper = Mappers.getMapper(RegistrationLocationMapper.class);
        RegistrationLocationEntity registrationLocationEntity = registrationLocationMapper.mapToRegistrationLocationEntity(registrationLocation);

        assertTrue(registrationLocationEntity.getDescription().startsWith(registrationLocation.getDescriptions().get(0).getValue()));
        assertTrue(registrationLocationEntity.getName().startsWith(registrationLocation.getNames().get(0).getValue()));
        assertEquals(registrationLocation.getGeopoliticalRegionCode().getValue(), registrationLocationEntity.getRegionCode());
        assertEquals(registrationLocation.getGeopoliticalRegionCode().getListID(), registrationLocationEntity.getRegionCodeListId());
        assertEquals(registrationLocation.getCountryID().getValue(), registrationLocationEntity.getLocationCountryId());
        assertEquals(registrationLocation.getCountryID().getSchemeID(), registrationLocationEntity.getLocationCountrySchemeId());
        assertEquals(registrationLocation.getTypeCode().getValue(), registrationLocationEntity.getTypeCode());
        assertEquals(registrationLocation.getTypeCode().getListID(), registrationLocationEntity.getTypeCodeListId());
        assertNull(registrationLocationEntity.getRegistrationEvent());
    }
}
