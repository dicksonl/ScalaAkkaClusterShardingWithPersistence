# ScalaAkkaClusterShardingWithPersistence

sbt clean compile 
sbt -DPORT=2551 -DROLE="seed"  run
sbt -DPORT=2552 -DROLE="worker"  run