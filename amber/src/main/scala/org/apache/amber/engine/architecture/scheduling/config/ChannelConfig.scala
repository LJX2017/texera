/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.amber.engine.architecture.scheduling.config

import org.apache.amber.core.virtualidentity.{ActorVirtualIdentity, ChannelIdentity}
import org.apache.amber.core.workflow._

case object ChannelConfig {
  def generateChannelConfigs(
      fromWorkerIds: List[ActorVirtualIdentity],
      toWorkerIds: List[ActorVirtualIdentity],
      toPortId: PortIdentity,
      partitionInfo: PartitionInfo
  ): List[ChannelConfig] = {
    partitionInfo match {
      case HashPartition(_) | RangePartition(_, _, _) | BroadcastPartition() | UnknownPartition() =>
        fromWorkerIds.flatMap(fromWorkerId =>
          toWorkerIds.map(toWorkerId =>
            ChannelConfig(ChannelIdentity(fromWorkerId, toWorkerId, isControl = false), toPortId)
          )
        )

      case SinglePartition() =>
        assert(toWorkerIds.size == 1)
        val toWorkerId = toWorkerIds.head
        fromWorkerIds.map(fromWorkerId =>
          ChannelConfig(ChannelIdentity(fromWorkerId, toWorkerId, isControl = false), toPortId)
        )
      case OneToOnePartition() =>
        fromWorkerIds.zip(toWorkerIds).map {
          case (fromWorkerId, toWorkerId) =>
            ChannelConfig(ChannelIdentity(fromWorkerId, toWorkerId, isControl = false), toPortId)
        }
      case _ =>
        List()

    }
  }
}

case class ChannelConfig(
    channelId: ChannelIdentity,
    toPortId: PortIdentity
)
