
🛠️ 学習プラットフォーム バックエンドプロジェクト

プロジェクト概要
コーディングやデザインを学ぶ若者が、互いに知識を共有しながらチームで小規模プロジェクトを進められる 学習プラットフォーム を開発しました。

ユーザーはアカウント登録後、学習チャンネルに参加し、知識や成果を共有可能

プロジェクトチャンネルを作成し、メンバーと共同開発ができる

スタディ投稿機能（自分が学んだスタディ内容）を通じて、学習記録を管理可能

ユーザー機能

ユーザー登録 / ログイン（JWT認証）

学習チャンネルの作成 / 参加

スタディ投稿の作成・編集・削除（自分が学んだスタディ内容）

プロジェクトチャンネル管理

プロフィール編集、アバター画像アップロード（Cloudflare R2）

管理者機能

ユーザー管理（権限付与/停止）

不適切投稿の削除

全体活動のモニタリング

使用技術スタック

バックエンド: Java 17, Spring Boot, JPA, Spring Security (JWT)

データベース: MySQL (Lightsail内)

インフラ: AWS Lightsail, Cloudflare R2

CI/CD: GitHub Actions, Docker

その他: Swagger, Validation, Docker Compose, GitHub Secrets

インフラ構成図

ユーザー → Lightsail (Spring Boot + MySQL)
          ↘ Presign URL → Cloudflare R2


CI/CD 自動デプロイ構成

GitHubにPush → GitHub Actions がトリガー


Lightsail サーバーへデプロイ (ssh)

application-prod.yml は GitHub Secrets から echo で生成

.env ファイル不要、安全に本番起動可能

トラブルシューティング

問題: study_images テーブル未生成 (Table 'cdy.study_images' doesn't exist)

原因: フィールド名 key が MySQL予約語

解決: @Column(name="object_key") に変更

結果: テーブル正常生成 & API呼び出し成功

教訓: 予約語は避け、必ず @Column でマッピング指定

🗓️ 開発期間: 2025年6月 ～ 2025年7月 (約4週間)
👤 開発者: 이동익 (Lee Dong Ik)
📧 Email: dongki9467@naver.com

🌐 GitHub: https://github.com/teotaku
