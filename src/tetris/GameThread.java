package tetris;

public class GameThread extends Thread {
	private GameArea ga;
	private GameForm gf;
	private NextBlockArea nba;
	private int score = 0;
	private int level = 1;
	private int scorePerLevel = 3; // 3개의 행이 삭제되면 레벨 상승
	private int pause = 1000;
	private int speedupPerLevel = 50;
	
	private boolean isPaused = false;

	public GameThread(GameArea ga, GameForm gf, NextBlockArea nba) {
		this.ga = ga;
		this.gf = gf;
		this.nba = nba;

		gf.updateScore(score);
		gf.updateLevel(level);
	}

	@Override
	public void run() {

		// 블록이 1초마다 1칸씩 떨어지도록
		while (true) {
			ga.spawnBlock(); // 새로운 블록 생성
			
			// 다음 블럭 설정
			ga.setNextBlock(); 
			nba.setNextBlock(ga.getNextBlock());
			
			while (ga.moveBlockDown()) {
				try {
					// Thread.sleep(pause);

					// 0.1초마다 pause키가 눌렸는지 확인
					// pause키가 눌렸으면 루프를 돌면서 대기 
					int i = 0;
					while(i < pause / 100) {
						Thread.sleep(100);
						i++;
						while(isPaused) {
							if(!isPaused) {
								break;
							}
						}
					}
				} catch (InterruptedException e) {
					// 메인 메뉴 버튼을 눌러서 GameThread가 인터럽트 되면 
					// 이 run 함수가 완전히 종료되도록!
					return; 
				}
			}

			// 쌓인 블록들이 보드판 경계를 넘어가면 게임 종료 
			if (ga.isBlockOutOfBounds()) {
				Tetris.gameOver(score);
				break;
			}

			// 보드판 경계를 넘지 않은 경우, 백그라운드 블록으로 전환 
			ga.moveBlockToBackground();
			
			// 삭제된 행의 개수 만큼 점수 증가
			score += ga.clearLines();
			gf.updateScore(score);

			// scorePerLevel 만큼 점수 얻으면 레벨 상승 
		 	int lvl = score / scorePerLevel + 1;
		 	if(lvl > level) {
		 		level = lvl;
		 		gf.updateLevel(level);
		 		pause -= speedupPerLevel; // 속도 증가
		 	}
		}
	}
	
	// 스레드 pause
	public void pause() {
		this.isPaused = true;
	}
	
	// 스레드 재시작
	public void reStart() {
		this.isPaused = false;
	}
}
