package hu.elte.inf.wiki.storage

import com.couchbase.client.scala.{Cluster, ClusterOptions}
import hu.elte.inf.wiki.Logger

class Couchbase(val cluster: Cluster) extends Logger {
  protected lazy val bucket = cluster.bucket("default")
  lazy val scope = bucket.defaultScope
}

object Couchbase extends Logger {
  def apply(): Couchbase = {
    new Couchbase(createCluster().get)
  }

  private def createCluster(): Option[Cluster] = {
    log.info("Connecting to Couchbase cluster.")
    Cluster.connect(
      connectionString = "localhost:8091",
      ClusterOptions.create(
        "Administrator",
        "123456"
      )
    ).toOption
  }
}