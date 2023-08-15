# UdpTrackerProxy

一个为 NexusPHP 等只有 HTTP/HTTPS 的 Tracker 设计的 UDP Tracker 代理工具。

<img width="796" alt="bd6f237d827902fd90bfbc53f7997a47" src="https://github.com/Ghost-chu/UdpTrackerProxy/assets/30802565/d290e034-d25e-498d-a3f1-372e1f42428b">
<img width="563" alt="fcf41d145baf219e25bb7ad319416b66" src="https://github.com/Ghost-chu/UdpTrackerProxy/assets/30802565/d1eee949-8026-4baf-9ba1-87716533d5d0">
<img width="1199" alt="2b0b100f81f83389573f3d9299fbf8cc" src="https://github.com/Ghost-chu/UdpTrackerProxy/assets/30802565/99f94a77-16b4-4152-9990-7b9320a15fd3">

以上图片中的 passkey 均已重置。

## 工作原理

程序启动 UDP Tracker，在收到请求后，会将请求转发到 HTTP Tracker，然后将 HTTP Tracker 的响应转发回去。  
该程序不能解决 Tracker 本身的性能问题，但可提供一种备用方案，用于用户与 Tracker 间 HTTP/HTTPS 协议不通的情况。

## 功能

* UDP Tracker 转换转发
* 支持多 HTTP Tracker 随机选择，负载均衡
* 整体/单 HTTP Tracker 并发控制
* 支持端口复用

## 项目引用

本项目使用了下列项目的源代码：

* [lafayette/udp-torrent-tracker (copyright)](https://github.com/lafayette/udp-torrent-tracker)
* [atomashpolskiy/bt (Apache-2.0)](https://github.com/atomashpolskiy/bt)
* [bitsapling/sapling (GPLv3)](https://github.com/bitsapling/sapling)