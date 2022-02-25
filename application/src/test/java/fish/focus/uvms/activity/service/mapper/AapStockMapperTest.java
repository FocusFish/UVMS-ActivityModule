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
import fish.focus.uvms.activity.fa.entities.AapStockEntity;
import fish.focus.uvms.activity.service.mapper.AapStockMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPStock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AapStockMapperTest {

    AapStockMapper aapStockMapper = Mappers.getMapper(AapStockMapper.class);

    @Test
    public void testAapStockMapper() {
        AAPStock aapStock = MapperUtil.getAapStock();
        AapStockEntity aapStockEntity = aapStockMapper.mapToAapStockEntity(aapStock);

        assertEquals(aapStock.getID().getValue(), aapStockEntity.getStockId());
        assertEquals(aapStock.getID().getSchemeID(), aapStockEntity.getStockSchemeId());
        assertNull(aapStockEntity.getFaCatch());
    }
}


