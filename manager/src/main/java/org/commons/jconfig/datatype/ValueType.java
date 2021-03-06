/*
 * Copyright 2011 Yahoo! Inc
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

package org.commons.jconfig.datatype;

import java.util.List;

import com.google.gson.JsonObject;

public enum ValueType {
    String {
        @Override
        public Class<String> classDefinition() {
            return String.class;
        }
    },
    Number {
        @Override
        public Class<Number> classDefinition() {
            return Number.class;
        }
    },
    Time {
        @Override
        public Class<TimeValue> classDefinition() {
            return TimeValue.class;
        }
    },
    Bytes {
        @Override
        public Class<ByteValue> classDefinition() {
            return ByteValue.class;
        }
    },
    Boolean {
        @Override
        public Class<Boolean> classDefinition() {
            return Boolean.class;
        }
    },
    Json {
        @Override
        public Class<JsonObject> classDefinition() {
            return JsonObject.class;
        }
    },
    StringList {
        @SuppressWarnings("rawtypes")
        @Override
        public Class<List> classDefinition() {
            return List.class;
        }
    },
    NumberList {
        @SuppressWarnings("rawtypes")
        @Override
        public Class<List> classDefinition() {
            return List.class;
        }
    },
    TimeList {
        @SuppressWarnings("rawtypes")
        @Override
        public Class<List> classDefinition() {
            return List.class;
        }
    },
    BooleanList {
        @SuppressWarnings("rawtypes")
        @Override
        public Class<List> classDefinition() {
            return List.class;
        }
    };

    public abstract Class<?> classDefinition();
}
