/*
 * Copyright (c) 2015, UltraMixer Digital Audio Solutions <info@ultramixer.com>, Seth J. Morabito <sethm@loomcom.com>
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.ultramixer.jarbundler;

public class LSEnvironment
{

    /**
     * The LSEnvironment' name and value
     */

    private String name = null;
    private String value = null;

    /**
     * Construct an empty JavaProperty
     */

    public LSEnvironment()
    {
    }

    /**
     * Get the LSEnvironment' name
     *
     * @return the LSEnvironment' name.
     */
    public String getName()
    {

        if (this.name == null)
        {
            return null;
        }

        return this.name.trim();
    }

    /**
     * Set the LSEnvironment's name; required
     *
     * @param name the LSEnvironment' name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the LSEnvironment' value.
     *
     * @return the LSEnvironment' value.
     */
    public String getValue()
    {

        if (this.value == null)
        {
            return null;
        }

        return this.value.trim();
    }

    /**
     * Set the LSEnvironment' value; required
     *
     * @param value the LSEnvironment' value
     */

    public void setValue(String value)
    {
        this.value = value;
    }

}
