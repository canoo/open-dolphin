/*
 * Copyright 2012-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opendolphin.demo.crud

import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot

import static org.opendolphin.demo.crud.PortfolioConstants.ATT.*
import static org.opendolphin.demo.crud.PositionConstants.ATT.*

class CrudInMemoryService implements CrudService {

    // Java-like variant
    List<DTO> listPortfolios(long ownerId) {
        List<DTO> result = new LinkedList<DTO>();
        result.add(new DTO(
            new Slot(DOMAIN_ID, 1),
            new Slot(NAME, 'Balanced'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        result.add(new DTO(
            new Slot(DOMAIN_ID, 2),
            new Slot(NAME, 'Growth'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        result.add(new DTO(
            new Slot(DOMAIN_ID, 3),
            new Slot(NAME, 'Risky'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        result.add(new DTO(
            new Slot(DOMAIN_ID, 4),
            new Slot(NAME, 'Insane'),
            new Slot(TOTAL, 100),
            new Slot(FIXED, false)
        ));
        return result;
    }

    // Groovy-like variant
    List<DTO> listPositions(long portfolioId) {
        [
            [(INSTRUMENT): 'ORCL', (WEIGHT): 10],
            [(INSTRUMENT): 'APPL', (WEIGHT): 40],
            [(INSTRUMENT): 'IBM',  (WEIGHT): 30],
            [(INSTRUMENT): 'UBSN', (WEIGHT): 20],
        ].collect { new DTO(Slot.slots(it)) }
    }
}
