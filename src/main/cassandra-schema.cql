create keyspace if not exists satishnagc_product with replication = {'class' : 'SimpleStrategy', 'replication_factor': 1 };


CREATE TABLE IF NOT EXISTS satishnagc_product.product_by_id_Name (
	productId text,
	productName text,
	timestamp timestamp,
	data varchar,
	PRIMARY KEY ( productId , productName)
) WITH CLUSTERING ORDER BY ( productName DESC )
AND gc_grace_seconds = 0
AND compression = {
	'sstable_compression' : 'SnappyCompressor'
}
AND compaction = {
	'class' : 'DateTieredCompactionStrategy',
	'timestamp_resolution' : 'MILLISECONDS'
};

