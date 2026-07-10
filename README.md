# Soon

**"요즘 볼 영화가 없다"** 는 일상의 불편에서 시작해, **2019년부터 혼자 만들고 운영해 온** 상영예정 영화 알림 앱입니다.

[![Google Play](https://img.shields.io/badge/Google_Play-다운로드-3DDC84?style=flat-square&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.lusle.android.soon)
[![시연 영상](https://img.shields.io/badge/YouTube-시연_영상-FF0000?style=flat-square&logo=youtube&logoColor=white)](https://www.youtube.com/shorts/y9WJL-Qq5_U)

![그래픽 이미지](https://drive.usercontent.google.com/download?id=10iWnyE3Zb5HNakbIpdBIMNzgSNC4kpSr)

| | |
|---|---|
| 기간 | 2019 ~ 현재 (고등학생 때 시작해 지금까지 운영) |
| 역할 | 1인 풀스택 (Android · 서버 · 인프라) |
| 서버 저장소 | [soon-server-side](https://github.com/Woochang4862/soon-server-side) |

## 📘 개요

영화의 상세 정보와 제작사 정보를 탐색하고, 관심 있는 영화의 **개봉 알림**을 받을 수 있는 안드로이드 앱입니다.
서버가 **1분마다 cron으로 영화 정보를 확인**해 변경이 감지되면 푸시 알림을 발송합니다.

- **영화 탐색** — 상영예정·인기 영화 목록과 상세 정보
- **제작사 정보** — 관심 제작사 팔로우
- **개봉 알림** — 관심 영화의 개봉일 푸시 알림
- **프로필 관리**

## 🏗️ 아키텍처 & 인프라

이 프로젝트가 남긴 건 기능이 아니라 **운영 경험**입니다.

- **클라이언트** — Kotlin · Android · MVVM + Jetpack (LiveData, ViewModel). 메모리 누수와 비정상 종료를 추적해 해결
- **서버** — Node.js · MySQL · Redis
- **배포** — `docker-compose`로 서비스 묶어 **AWS EC2**에 배포
- **알림** — cron(1분 주기) → 변경 감지 → Firebase Cloud Messaging 푸시
- **비용 제약** — 제한된 예산 탓에 EC2 인스턴스를 **여러 번 이전**해야 했고, 그 과정에서 *환경 설정을 자동화해두지 않으면 이전 비용이 매번 처음부터 발생한다*는 것을 배웠습니다

## 🛠️ 기술 스택

**Client** ![Kotlin](https://img.shields.io/badge/Kotlin-blue) ![Android](https://img.shields.io/badge/Android-blue) ![MVVM](https://img.shields.io/badge/Architecture-MVVM-green) ![Retrofit](https://img.shields.io/badge/Retrofit-yellow) ![LiveData](https://img.shields.io/badge/LiveData-yellow)

**Server & Infra** ![Node.js](https://img.shields.io/badge/Node.js-339933?logo=node.js&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white) ![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white) ![Docker](https://img.shields.io/badge/docker--compose-2496ED?logo=docker&logoColor=white) ![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?logo=amazonec2&logoColor=white) ![FCM](https://img.shields.io/badge/Firebase_Cloud_Messaging-FFCA28?logo=firebase&logoColor=black)
