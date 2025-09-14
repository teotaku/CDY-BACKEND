
💻 コーディング × デザイン 学習プラットフォーム（バックエンド）
プロジェクト概要

コーディングやデザインを学ぶ若者が、
互いに必要な知識をシェアしながら チームでトイプロジェクトを進められる学習プラットフォーム を開発しました。

ユーザーはアカウント登録後、学習チャンネルに参加し、知識を共有可能

プロジェクトチャンネルを作成し、メンバーと共同開発

投稿・コメント機能を通じて、学習記録とフィードバックを実現

ユーザー機能

ユーザー登録 / ログイン（JWT認証）

学習チャンネルの作成 / 参加

投稿・コメント（CRUD）

プロジェクトチャンネル管理

プロフィール編集、アバター画像アップロード（Cloudflare R2）

管理者機能

ユーザー管理（権限付与/停止）

不適切投稿の削除

全体統計のモニタリング

用技術スタック
| 分野     | 技術                                                             |
| ------ | -------------------------------------------------------------- |
| バックエンド | Java 17, Spring Boot, JPA, Spring Security (JWT)               |
| データベース | MySQL (Lightsail内蔵), H2 (テスト用)                                 |
| インフラ   | AWS Lightsail（App + DB統合）, Cloudflare R2（Presign URL）          |
| CI/CD  | GitHub Actions, Docker                                         |
| その他    | Swagger（OpenAPI 3）, Validation, Docker Compose, GitHub Secrets |

インフラ構成図
ユーザー → Lightsail (Spring Boot + MySQL)
          ↘ Presign URL → Cloudflare R2

          
環境分離構成

dev: Docker Compose + R2接続（MySQL/Redisはローカル）

prod: Lightsail + Cloudflare R2 + GitHub Actions（自動デプロイ）

test: H2 + Mock（外部依存を排除）

実行方法

# 開発環境
./gradlew bootRun -Dspring.profiles.active=dev

# テスト環境
./gradlew test -Dspring.profiles.active=test


CI/CD 自動デプロイ構成

GitHub Push → Docker ビルド → Lightsail デプロイまで自動化

application-prod.yml は GitHub Secrets により echo で生成

.env 不要で安全に稼働可能

導入背景と効果

背景: 開発・テストを本番環境で実行すると、コスト増大と速度低下の問題

対応: 軽量なローカルDocker構成で迅速な開発、Mockで外部依存排除

効果:

AWS費用削減

開発・テスト効率向上

CI/CDによる安定したデプロイ

🗓️ 開発期間: 2025年8.1月 ～ 2025年9月（約8週間）
👤 開発者: 이동익 (Lee Dong Ik)
📧 Email: dongki9467@naver.com

🌐 GitHub: https://github.com/teotaku
