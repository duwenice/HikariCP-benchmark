/*
 * Copyright (C) 2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaxxer.hikari.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

//@State(Scope.Benchmark)
//@Warmup(iterations=3, batchSize=1_000_000)
//@Measurement(iterations=8, batchSize=1_000_000)
//@BenchmarkMode(Mode.SingleShotTime)
//@OutputTimeUnit(TimeUnit.NANOSECONDS)

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.annotations.CompilerControl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 8)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PreparedStatementBench extends BenchBase {

    @Benchmark
    @CompilerControl(CompilerControl.Mode.INLINE)
    public PreparedStatement cyclePreparedStatement(Blackhole bh, ConnectionState state) throws SQLException {
        PreparedStatement ps = state.connection.prepareStatement("INSERT INTO test (column) VALUES (?)");
        ps.setString(1, "someValue");  // 模拟 ORM 里 set 参数
        bh.consume(ps.executeUpdate()); // INSERT / UPDATE / SELECT 都可以换
        ps.close();
        return ps;
    }

    @State(Scope.Thread)
    public static class ConnectionState {
        Connection connection;

        @Setup(Level.Iteration)
        public void setup() throws SQLException {
            connection = DS.getConnection();
        }

        @TearDown(Level.Iteration)
        public void teardown() throws SQLException {
            connection.close();
        }
    }
}
