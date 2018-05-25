/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.kurtraschke.nyctrtproxy.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

public class SanityTestNewData extends SanityTest {

    @Override
    @Before
    public void before() {
        setAgencyId("MTASBWY");
        Injector injector = Guice.createInjector(getTestModule("subways_from_atis.zip", "MTASBWY", false));
        injector.injectMembers(this);
    }

    @Test
    public void test1_2018_05_09() throws Exception {
        test(1, "1_2018-05-09.pb", 88, 123);
    }

    @Test
    public void test51_2018_05_09() throws Exception {
        test(51, "51_2018-05-09.pb", 9, 21);
    }

}
