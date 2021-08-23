--Test DATA
--Add vehicles
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-1',100,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-2',50,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-3',10,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-4',150,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-5',200,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-6',100,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-7',120,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-8',110,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-9',80,now(),now());
insert into vehicle (name,capacity,created_Date,update_Date) values ('V-10',1,now(),now());

--add tariffs
insert into tariff (name, rate, discount,created_Date,update_Date) values ('T-1',5,5,now(),now());
insert into tariff (name, rate, discount,created_Date,update_Date) values ('T-2',3,0,now(),now());
insert into tariff (name, rate, discount,created_Date,update_Date) values ('T-3',10,10,now(),now());
insert into tariff (name, rate, discount,created_Date,update_Date) values ('T-4',2,1,now(),now());
insert into tariff (name, rate, discount,created_Date,update_Date) values ('T-5',4,5,now(),now());

-- assign vehicle to tariff
insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-1','V-1');
insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-1','V-2');

insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-2','V-3');
insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-2','V-4');
insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-2','V-5');

insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-3','V-6');
insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-3','V-7');

insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-4','V-8');
insert into TARIFF_APPLICABLE_VEHICLE (tariff_name,vehicle_name) values ('T-4','V-9');

-- add shipments
insert into shipment (name, weight, created_Date,update_Date) values ('SH-1',5, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-2',15, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-3',25, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-4',20, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-5',10, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-6',55, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-7',35, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-8',25, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-9',15, now(), now());
insert into shipment (name, weight, created_Date,update_Date) values ('SH-10',45, now(), now());
