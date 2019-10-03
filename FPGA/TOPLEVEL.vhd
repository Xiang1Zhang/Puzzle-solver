library IEEE;
use IEEE.std_logic_1164.all;
use ieee.numeric_std.all;
use work.all;

entity TOPLEVEL is
	port(
		GPIO_0		: inout std_logic_vector(26 downto 0);
		CLOCK_50	: in std_logic;
		KEY		: in std_logic_vector(3 downto 0);
		TD_CLK27	: in std_logic;
		TD_DATA		: in std_logic_vector(7 downto 0);
		TD_HS		: in std_logic;
		TD_VS		: in std_logic;
		TD_RESET_N	: inout std_logic;

		-- Debugging
		LEDR		: out std_logic_vector(9 downto 0);
		HEX0		: out std_logic_vector(6 downto 0);
		HEX1		: out std_logic_vector(6 downto 0);
		HEX2		: out std_logic_vector(6 downto 0);
		HEX3		: out std_logic_vector(6 downto 0);
		HEX4		: out std_logic_vector(6 downto 0);
		HEX5		: out std_logic_vector(6 downto 0);
		
		
		DEBUG		: out std_logic;
		DB0 		: out std_logic;
		DB1		: out std_logic_vector(7 downto 0);
		DB2		: out std_logic_vector(7 downto 0);
		DB3		: out std_logic
	);
end entity;

architecture A of TOPLEVEL is

	-- FUNCTIONS --
	---------------

	-- Display nibble to HEX display
	function hexd(n:std_logic_vector(3 downto 0)) return std_logic_vector is
	begin
		case n is          --        	 gfedcba
	    	WHEN "0000" => RETURN NOT 	"0111111";
	    	WHEN "0001" => RETURN NOT 	"0000110";
	    	WHEN "0010" => RETURN NOT 	"1011011";
	    	WHEN "0011" => RETURN NOT 	"1001111";
	    	WHEN "0100" => RETURN NOT 	"1100110";
	    	WHEN "0101" => RETURN NOT 	"1101101";
	    	WHEN "0110" => RETURN NOT 	"1111101";
	    	WHEN "0111" => RETURN NOT 	"0000111";
	    	WHEN "1000" => RETURN NOT 	"1111111";
	    	WHEN "1001" => RETURN NOT 	"1101111";
	    	WHEN "1010" => RETURN NOT 	"1110111";
	    	WHEN "1011" => RETURN NOT 	"1111100";
	    	WHEN "1100" => RETURN NOT 	"0111001";
	    	WHEN "1101" => RETURN NOT 	"1011110";
	    	WHEN "1110" => RETURN NOT 	"1111001";
	    	WHEN OTHERS => RETURN NOT 	"1110001";			
    	end case;
	end hexd;
	
	-- SIGNALS --
	-------------
	
	-- RPI signals
	signal RPIDAT		: std_logic_vector(7 downto 0);
	signal RPICLK		: std_logic;
	signal RPIHLD		: std_logic;
	signal RPIREQ		: std_logic;
	
	-- System signals
	signal s_clk		: std_logic;
	signal s_reset		: std_logic;
	
	-- Memory signals
	signal r_adr		: std_logic_vector(16 downto 0);
	signal w_adr		: std_logic_vector(16 downto 0);
	signal w_dat		: std_logic_vector(7 downto 0);
	signal w_wr			: std_logic;
	
	-- Debug signal
	signal r_filling	: std_logic;
	
	-- Edges
	signal re_RPICLK 	: std_logic;
	signal fe_RPICLK	: std_logic;
	
	-- Video decoder
	signal VDC_dat		: std_logic_vector(7 downto 0);
	signal VDC_clk		: std_logic;
	signal VDC_HS		: std_logic;
	signal VDC_VS		: std_logic;
		-- signals to RESIZE
	signal vr_dat		: std_logic_vector(7 downto 0);
	signal vr_clk		: std_logic;

	-- Resizing
	signal itr		: std_logic;
	signal image_done	: std_logic;
	
begin

	-- MAPPING --
	-------------

	-- Mapping RPI signals to IO
	GPIO_0(15 downto 9) 			<= RPIDAT(6 downto 0);
	GPIO_0(4)				<= RPIDAT(7);
	GPIO_0(21)				<= RPIHLD;
	RPICLK					<= GPIO_0(24);
	RPIREQ					<= GPIO_0(25);
	
	-- Mapping system signals to IO
	s_reset					<= KEY(0);
	s_clk					<= CLOCK_50;

	-- Mapping VDC to signals
	VDC_dat 				<= TD_DATA;
	VDC_clk					<= TD_CLK27;
	VDC_hs					<= TD_HS;
	VDC_vs					<= TD_VS;
	TD_RESET_N				<= s_reset;
	
	-- Mapping signals to DEBUG LEDS
		-- RPIDAT to LEDS
	LEDR(7 downto 0) <= RPIDAT;
		-- RPIHLD, RPICLK, RPIREQ to HEX0
	hex0 <= not RPIHLD & not RPICLK & not RPIREQ & not r_filling & "111";
		-- w_adr to HEX when filling, else r_adr
	hex1 <= hexd(r_adr(3 downto 0)) when r_filling='0' else hexd(w_adr(3 downto 0));
	hex2 <= hexd(r_adr(7 downto 4)) when r_filling='0' else hexd(w_adr(7 downto 4));
	hex3 <= hexd(r_adr(11 downto 8)) when r_filling='0' else hexd(w_adr(11 downto 8));
	hex4 <= hexd(r_adr(15 downto 12)) when r_filling='0' else hexd(w_adr(15 downto 12));
	hex5 <= hexd("000"&r_adr(16)) when r_filling='0' else hexd("000"&w_adr(16));
	
	DB0 <= vr_clk;
	DB1 <= vr_dat;
	DB2 <= w_dat;
	DB3 <= W_wr;

	-- COMPONENTS --
	----------------
	
	-- 128Kb RAM
	e_ram : entity RAM(A) port map(
		s_reset,
		s_clk,
		RPIDAT,
		r_adr,
		w_dat,
		w_adr,
		w_wr
	);

	-- Video decoder
	e_vde : entity BT656(A) port map(
		VDC_dat,
		VDC_clk,
		VDC_HS,
		VDC_VS,
		vr_dat,
		vr_clk,
		s_reset,
		itr,
		DEBUG
	);

	-- Image resizing
	e_res : entity work.RESIZE(A) port map(
		vr_clk,
		vr_dat,
		itr,
		image_done,
		s_clk,
		s_reset,
		w_dat,
		w_adr,
		w_wr
	);

	-- COMMUNICATION --
	-------------------
	
	proc : process(s_clk, s_reset)
		
		-- State machine
		variable state 		: integer;
		
		-- EDV's
		variable ed_RPIREQ	: std_logic;
		variable ed_RPICLK	: std_logic;
		
		-- RAM fill
		variable rf_cnt		: integer;
		variable rf_wrclk	: std_logic;
		
		-- RAM read
		variable zstart		: std_logic;
		variable rr_hldcnt	: integer;
		variable rr_cnt		: integer;
		variable rr_cnt_old	: integer;
		
	begin
		if(s_reset='0') then
			-- Reset state machine
			state := 0;
			ed_RPIREQ:='0'; 
			ed_RPICLK:='0';
			re_RPICLK<='0';
			fe_RPICLK<='0';
		elsif(rising_edge(s_clk)) then
			
			-- RPIREQ edge detection
			if(ed_RPIREQ='0' and RPIREQ='1') then		-- Rising edge
				ed_RPIREQ:='1';
				-- Reset whole system -> resetting state machine
				state := 0;
				-- Outputting a HLD
				RPIHLD <= '1';
			elsif(ed_RPIREQ='1' and RPIREQ='0') then	-- Falling edge
				ed_RPIREQ:='0';
			end if;
			
			-- RPICLK edge detection
			--re_RPICLK:='0';
			--fe_RPICLK:='0';
			if(ed_RPICLK='0' and RPICLK='1') then		-- Rising edge
				ed_RPICLK:='1';
				re_RPICLK<='1';
				fe_RPICLK<='0';
			elsif(ed_RPICLK='1' and RPICLK='0') then	-- Falling edge
				ed_RPICLK:='0';
				fe_RPICLK<='1';
				re_RPICLK<='0';
			end if;
			
			case state is
				when 0 => -- System reset / Idle
				
					-- Memory signal reset
					r_adr <= '0'&X"0000";
					--w_adr <= '0'&X"0000";
					--w_dat <= X"00";
					--w_wr <= '0';
					
					-- Ram fill
					rf_cnt := 0;
					rf_wrclk := '0';
					
					-- Ram read
					zstart := '0';
					rr_cnt := 0;
					rr_cnt_old := 0;
					
					-- Debug signals
					r_filling <= '0';

					-- IO
					RPIHLD <= '0'; 

					-- Resize
					itr <= '0'; 
					
					-- REQ? then next state
					if(RPIREQ='1') then state := 1; end if;
					
				when 1 => -- RAM FILL (normally waiting for image_done signal
						  -- now filling ram with dummy data
					r_filling <= '1';
					RPIHLD <= '1';

					itr<='1';
					if(image_done='1') then 
						-- RAM FULL -> image_done
						RPIHLD <= '0';
						state := 2;
						r_filling <= '0';
					end if;

					--RPIHLD <= '1';
					--if(rf_wrclk='0') then 
					--	w_adr <= std_logic_vector(to_unsigned(rf_cnt, 17));
					-- if(rf_cnt=0) then w_dat <= X"01"; end if;
					--	if(rf_cnt=1) then w_dat <= X"68"; end if;
					-- if(rf_cnt=2) then w_dat <= X"01"; end if;
					-- if(rf_cnt=3) then w_dat <= X"18"; end if;
					--	if(rf_cnt>3) then w_dat <= std_logic_vector(to_unsigned(rf_cnt, 8)); end if;
					--	w_wr <= '1';
					--	rf_cnt := rf_cnt+1;
					--	if(rf_cnt>=360*288+4) then -- 360*288+4
					--		-- RAM FULL -> image_done
					--		RPIHLD <= '0';
					--		state := 2;
					--		r_filling <= '0';
					--	end if;
					--else w_wr <= '0'; end if;
					
				when 2 => -- Increase address counter
					if(zstart='0') then zstart:='1'; state:=4; else 
					rr_cnt_old:=rr_cnt;
					rr_cnt:=1+rr_cnt;
					if(rr_cnt=rr_cnt_old+1) then state:=4; end if; 
					end if;
					
					
				when 3 => -- Wait for HLD counter
					rr_hldcnt:=1+rr_hldcnt;
					if(rr_hldcnt>=10) then
						rr_hldcnt := 0;
						RPIHLD <= '0';
						-- Wait for falling edge. If so start sending next byte
						--if(fe_RPICLK='1') then state:=2; end if;
						state := 2;
					end if;
				
				when 4 => -- Wait for rising edge CLK and output data
					if(re_RPICLK='1') then
						re_RPICLK<='0';
						-- Increase read counter (skip the first clk)
						r_adr <= std_logic_vector(to_unsigned(rr_cnt, 17));
						-- Output HLD
						RPIHLD <= '1';
						rr_hldcnt := 0;		-- Set HLD counter to 0
						state := 3;
					end if;

				-- Unreachable state is reached -> Reset system
				when others => state:=0;
			end case;
			
		end if;
	end process;

end architecture;


