# PemantauanWSN

Nama : Dandy Unggana

Skripsi : Pengembangan Aplikasi Pemantauan Wireless Sensor Network

Dosen Pembimbing : Elisati Hulu, M.T.

------------------------------------------------------------------------------------------

Petunjuk Penggunaan :
A. Program Yang Harus Disiapkan:
1. Eclipse IDE
2. Java JDK & JRE
3. Jar Apache Ant

B. Perangkat Keras Yang Diperlukan:
1. Node Sensor Preon32.
2. Baterai 9v
3. Kabel USB Tipe A

C. Langkah Instalasi
1. Siapkan semua program yang dibutuhkan pada komputer.
2. Aturlah PATH yang digunakan pada system variable komputer anda (Ant, Java jdk, jre, eclipse)
3. Buka Eclipse IDE
4. Masukan 2 Project ke dalam workspace pada Eclipse.
5. Project pertama yaitu UI. Project ini digunakan untuk menangani program yang memiliki hubungan antara 
	Base Station dengan Komputer pengguna dan sebagai antarmuka bagi pengguna.
6. Project kedua yaitu Sandbox. Pada project ini yang menangani pemrograman pada setiap node sensor.

D. Langkah Menambahkan dan Mengatur Context
Context digunakan untuk mengatur setiap node sensor menyimpan kode program.
1. Pada folder Sandbox file "buildUser.xml" tambahkan context baru dengan:

	<target name="context.set.1">
		<switchContext to="config/context1.properties" />
	</target>

2. Pada folder Sandbox/config, buat file sesuai dengan switchContext pada nomor 1 (context1.properties)
3. Atur context sesuai dengan nama class, nama module, dan nama port yang digunakan untuk melakukan transfer data ke node sensor.
4. Jika menggunakan baterai maka module.name harus diisi dengan "autostart", namun jika menjalankan program node sensor melalui ant script maka module.name harus diisi dengan nama lain selain "autostart"

E. Mengatur PATH untuk gambar node sensor
1. Folder gambar yang ada pada folder project digunakan untuk visualisasi tampilan node sensor ketika menyala/mati.
2. Sesuaikan PATH yang ada pada kelas GUI untuk gambar node sensor.

F. Mengatur PATH untuk nama node sensor
1. Pada folder nama terdapat file .txt yang berisikan nama-nama node sensor (dapat diganti sesuai keinginan selama memakai 4 karakter heksadesimal).
2. Aturlah PATH pada kelas SensorName yang terdapat pada project UI agar nama-nama node sensor dapat masuk ke dalam program.
 
G. Langkah Mengatur Address Base Station
1. Buka kelas BaseStation yang terdapat pada project sandbox.
2. Ada atribut "Base" sebagai alamat dari BaseStation, dapat diubah selama memakai 4 karakter heksadesimal (sesuaikan alamat BASE pada kelas Node)
3. Untuk pengiriman pesan secara BROADCAST sehingga tidak perlu diatur lagi.
4. Untuk atribut array "alladdress" (samakan isi atribut ini dengan yang ada pada folder nama pada project UI) 
dapat digunakan jika ingin mencoba mengirim satu per satu ke node dengan catatan node tersebut menyala.

H. Langkah Mengatur Address Node Sensor
1. Buka kelas Node yang terdapat pada project sandbox.
2. Atribut Node berisi alamat node yang dapat diisi dari atribut "alladdress" (samakan isi atribut ini dengan yang ada pada folder nama pada project UI)
3. Atribut Base berisi alamat BaseStation (sesuaikan isi alamatnya dengan kelas BaseStation) untuk pengiriman hasil sensing.


I. Langkah Mengunggah Program Ke Dalam Node Sensor.
1. Atur context yang digunakan (nama kelas, module, dan comport yang digunakan).
2. Pada menu ant script "Preon32 Sandbox User" pilih context yang telah di atur sebelumnya dengan cara klik 2x.
3. Pada menu ant script "Preon32 Sandbox" pilih perintah ".all" untuk menunggah program.
4. Dapat juga menggunakan perintah "cmd.module.upload", "cmd.module.run" secara berurutan (jika gagal gunakan cara nomor 3)
5. Setelah program berhasil diunggah dan dijalankan maka akan muncul console sebagai tampilan pengguna.

J. Mengatur GUI
GUI digunakan sebagai perantara antara program komputer dengan base station. GUI harus diatur sesuai dengan port yang digunakan base station.
1. Pada file GUI.java terdapat beberapa hal yang harus diubah sesuai dengan kebutuhan.
2. Pada method init() ubah nama port yang digunakan oleh base station. (Base station terhubung dengan port yang mana pada komputer).
Preon32Helper nodeHelper = new Preon32Helper("COM8", 115200); pada contoh tersebut maka base station terhubung dengan port COM8.
3. Sesuaikan nama module yang telah diunggah ke dalam base station pada atribut "DataConnection conn" di method tersebut.

K. Menjalankan Program Secara Bersamaan.
1. Pastikan program pada node sensor sudah berjalan. (Jika menggunakan baterai maka baterai harus sudah terpasang, dan module yang diunggah haruslah "autostart". Jika tidak menggunakan baterai, program harus dijalakan melalui ant script)
2. Jalankan Program GUI.java
3. Tekan tombol start untuk memulai sensing.


L. Tambahan
1. Setiap port pada komputer akan memiliki nama yang berbeda saat dipasang oleh node sensor yang berbeda. Ini harus diperhatikan, jika salah maka program tidak dapat diupload.
2. Jika ada beberapa orang yang sedang menggunakan WSN pada waktu yang sama, maka haruslah memiliki PAN_ID yang berbeda agar tidak terjadi bentrok pada jaringan tersebut.
